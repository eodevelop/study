## 순서
### 1. ConfigMap 만들기
- RabbitMq 구성 파일뿐 아니라 Consumer, Publisher 쪽에서도 사용될 ConfigMap 파일을 아래와 같이 구성한다.
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
```
- 해당 파일을 생성후 `kubectl apply -f 파일명.yaml` 로 실행

### 2. Deployment 생성
- RabbitMq를 생성하기 위한 yaml 파일 생성
    - 아래는 현재 Deployment로 생성하였지만 추후 데이터의 보존을 위해 kind와 Volume을 변경할 필요 있음
``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rabbitmq-deployment
spec:
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
        image: rabbitmq:3-management        # RabbitMQ 관리 UI가 포함된 이미지
        ports:
        - containerPort: 5672               # AMQP 포트 (내부 접근용)
        - containerPort: 15672              # 관리 UI 포트 (외부 접근용)
        env:
        - name: RABBITMQ_DEFAULT_USER        # 기본 사용자 이름
          valueFrom:
            configMapKeyRef:
              name: rabbitmq-config
              key: RABBITMQ_USERNAME
        - name: RABBITMQ_DEFAULT_PASS        # 기본 비밀번호
          valueFrom:
            configMapKeyRef:
              name: rabbitmq-config
              key: RABBITMQ_PASSWORD
``` 

### 3. 관리 UI 접근 및 내부 통신을 위한 NodePort Service 추가
- 현재 deployment와 별도 구성을 하긴 했지만 상황에 따라 --- 로 구분 후 하나로 합쳐도 괜찮음.
```yaml
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

### 4. 관리 UI에서 사용할 Queue 생성하기
- Queue를 yaml 내부 구성 혹은 초기화 프로젝트로도 생성 가능하지만 테스트용으로 간단한 생성을 하기 위해 관리자 UI를 통해 생성
- `<node의IP>:32003` 주소로 브라우저에서 접속한다.
- 위에서 설정한 계정 정보인 `guest:guest`로 로그인
- `Quesues and Streams ` 탭으로 이동 
- `Add a new queue` 토글 클릭하여 펼치기
  - Name: 원하는 Queue 이름을 입력합니다 (예: my-queue).
  - Durability: Durable을 체크하여 재시작 시에도 Queue가 유지되도록 설정합니다.
  - Auto delete: 필요 시 체크합니다. (일정 조건 하에 Queue가 자동 삭제되도록 설정)
  - 나머지 설정은 기본값으로 둡니다.
  - Add queue 버튼을 클릭하여 Queue를 생성합니다.