## 개요
### 전내용
- [전내용 링크](./02_단일%20RabbitMq%20StatefulSet,PVC%20로%20변경하기%20.md)
- deployment로 구성된 RabbitMq를 StatefulSet으로 변경하여 PVC를 통해 데이터를 보존할 수 있도록 함.
### 변경할 내용
- 단일 Replica로 구성된 RabbitMq를 다중 Replica로 변경

## 순서
### 1. StatefulSet 관련 yaml 파일 변경
- 기존의 StatefulSet 용 yaml 파일을 아래와 같이 변경
``` yaml
apiVersion: v1
kind: Secret
metadata:
  name: rabbitmq-erlang-cookie
type: Opaque
data:
  .erlang.cookie: "bXlzZWNyZXRjb29raWV2YWx1ZQ=="
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: rabbitmq
spec:
  serviceName: "rabbitmq-service"
  replicas: 2
  selector:
    matchLabels:
      app: rabbitmq
  template:
    metadata:
      labels:
        app: rabbitmq
    spec:
      initContainers:
      - name: init-rabbitmq
        image: busybox
        command: ['sh', '-c', 'cp /tmp/erlang.cookie /var/lib/rabbitmq/.erlang.cookie && chown 999:999 /var/lib/rabbitmq/.erlang.cookie && chmod 400 /var/lib/rabbitmq/.erlang.cookie']
        volumeMounts:
        - name: rabbitmq-data
          mountPath: /var/lib/rabbitmq
        - name: erlang-cookie
          mountPath: /tmp/erlang.cookie
          subPath: .erlang.cookie
      containers:
      - name: rabbitmq
        image: rabbitmq:3-management     # RabbitMQ 관리 UI가 포함된 이미지
        ports:
        - name: amqp
          containerPort: 5672            # AMQP 포트
        - name: management
          containerPort: 15672           # 관리 UI 포트
        env:
        - name: MY_POD_NAME
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: RABBITMQ_NODENAME
          value: "rabbit@$(MY_POD_NAME)"
        - name: RABBITMQ_USE_LONGNAME
          value: "true"
        - name: RABBITMQ_DEFAULT_USER
          valueFrom:
            configMapKeyRef:
              name: rabbitmq-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_DEFAULT_PASS
          valueFrom:
            configMapKeyRef:
              name: rabbitmq-config
              key: RABBITMQ_PASSWORD
        volumeMounts:
        - name: rabbitmq-data
          mountPath: /var/lib/rabbitmq
        - name: rabbitmq-config-file
          mountPath: /etc/rabbitmq/rabbitmq.conf
          subPath: rabbitmq.conf
      volumes:
        - name: rabbitmq-config-file
          configMap:
            name: rabbitmq-config-file
        - name: erlang-cookie
          secret:
            secretName: rabbitmq-erlang-cookie
  volumeClaimTemplates:
  - metadata:
      name: rabbitmq-data
    spec:
      storageClassName: "nfs-storage"
      accessModes:
        - ReadWriteMany
      resources:
        requests:
          storage: 1Gi
---
apiVersion: v1
kind: Service
metadata:
  name: rabbitmq-service
spec:
  selector:
    app: rabbitmq
  ports:
    - name: amqp                    # AMQP 포트 이름 추가
      protocol: TCP
      port: 5672                    # AMQP 포트, 내부 통신용
      targetPort: 5672
    - name: management               # 관리 UI 포트 이름 추가
      protocol: TCP
      port: 15672                   # 관리 UI 포트, 외부 접근용
      targetPort: 15672
      nodePort: 32003               # 외부에서 UI 접근을 위한 NodePort
  type: NodePort                    # NodePort는 UI 접근만을 위해 설정
``` 
- 차이점
  - replicas 가 1에서 3으로 변경
  - ports의 이름을 amqp, management로 명시
  - MY_POD_NAME 환경 변수 추가
    - 각 RabbitMq 인스턴스가 고유한 이름을 가지게하여 클러스터 내에서 다른 인스턴스와 구별되게 한다.
    - `MY_POD_NAME`: 환경 변수로, 현재 Pod의 고유 이름을 가져옵니다.
    - `valueFrom`: Pod의 메타데이터에서 값을 가져오는 방식입니다.
    - `fieldRef와 fieldPath`: metadata.name을 사용해 현재 Pod의 이름을 참조합니다. 예를 들어, rabbitmq-0, rabbitmq-1 등으로 자동 할당됩니다.
  - RABBITMQ_NODENAME 환경 변수 추가
    - RabbitMQ는 노드 이름을 기반으로 클러스터를 구성하므로, 이 설정은 클러스터 내에서 인스턴스 간의 충돌을 방지하고, 고유한 인스턴스로 인식되도록 합니다.
    - `RABBITMQ_NODENAME`: RabbitMQ 노드 이름을 설정하는 환경 변수입니다.
    - `값`: "rabbit@$(MY_POD_NAME)"로, MY_POD_NAME의 값이 동적으로 적용되어 각 Pod가 고유한 노드 이름을 가지도록 합니다. 예를 들어 rabbit@rabbitmq-0, rabbit@rabbitmq-1 등으로 설정됩니다.
  - RABBITMQ_USE_LONGNAME 환경 변수 추가
    - Kubernetes에서는 노드가 FQDN을 사용해 접근하므로, 이 설정은 RabbitMQ 인스턴스가 클러스터링 시 FQDN을 기반으로 통신하도록 설정하여, 노드 간 통신에 문제가 생기지 않도록 합니다.
    - `RABBITMQ_USE_LONGNAME`: RabbitMQ가 FQDN을 사용해 통신하도록 설정하는 환경 변수입니다.
  - rabbitmq-config-file 관련 설정
    - 이 볼륨은 RabbitMQ 설정 파일을 저장하기 위해 사용됩니다.
    - ConfigMap rabbitmq-config-file을 참조하여 생성되는 파드의 설정 파일인 /etc/rabbitmq/rabbitmq.conf 파일을 마운트합니다.

### 2.rabbitmq-config 관련 yaml 파일 변경
```yaml 
apiVersion: v1
kind: ConfigMap
metadata:
  name: rabbitmq-config
data:
  RABBITMQ_HOST: rabbitmq-service
  RABBITMQ_PORT: "5672"
  RABBITMQ_USERNAME: guest
  RABBITMQ_PASSWORD: guest
  RABBITMQ_QUEUE: my-queue
```

### 3. rabbitmq-config-file 관련 yaml 파일 생성
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: rabbitmq-config-file
data:
  rabbitmq.conf: |
    cluster_formation.peer_discovery_backend = k8s
    cluster_formation.k8s.host = rabbitmq-service
    cluster_formation.node_cleanup.interval = 10
    cluster_formation.node_cleanup.only_log_warning = true
    queue_master_locator = min-masters
```


## 해당 방식 살짝 문제 있는것 같아서 일단 보류
- 클러스터간의 통신 제대로 안됌
- 계속해서 꺼졌다 켜졌다 반복됨 어디의 문제인지 찾아봐야 할듯
- [RabbitMQ Cluster Kubernetes Operator Quickstart](https://www.rabbitmq.com/kubernetes/operator/quickstart-operator) 으로 다시 시도해볼 예정
  - 해당 방식은 커스터마이징 문제가 좀 있을것 같아 테스트용으로만