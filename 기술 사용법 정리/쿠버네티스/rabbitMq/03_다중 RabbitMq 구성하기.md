## 개요
### 전내용
- [전내용 링크](./02_단일%20RabbitMq%20StatefulSet,PVC%20로%20변경하기%20.md)
- 위 링크에서는 단일 RabbitMq를 구성하는 방법을 설명함.
### 변경할 내용
- 기존에 deployment 로 만들어서 pod가 새로 생성될 경우 queue의 데이터가 사라지는 문제가 있음.
- 이를 해결하기 위해 StatefulSet을 사용하여 pod가 새로 생성되어도 PVC를 통해 데이터를 보존할 수 있도록 함.

## 순서
### 1. yaml 파일 변경
- 기존의 yaml 파일을 아래와 같이 변경
``` yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: rabbitmq
spec:
  serviceName: "rabbitmq-service"
  replicas: 1
  selector:
    matchLabels:
      app: rabbitmq
  template:
    metadata:
      labels:
        app: rabbitmq
    spec:
      containers:
      - name: rabbitmq
        image: rabbitmq:3-management
        ports:
        - containerPort: 5672                   # AMQP 포트
        - containerPort: 15672                  # 관리 UI 포트
        env:
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
          mountPath: /var/lib/rabbitmq            # RabbitMQ 데이터 디렉터리
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
``` 
- 차이점
  - serviceName: "rabbitmq-service"
    - 기존 deployment에서와는 달리 네트워크ID를 직접 명시하며 pod가 위 이름을 기준으로 생성됌.
    - 또한 이를통해 인덱스와 생성순서들이 명확해짐
  - volumeMounts
    - volumeMounts를 통해서 PVC를 통해 데이터를 보존할 수 있도록 함.
  - volumeClaimTemplates
    - PVC를 생성하기 위한 설정