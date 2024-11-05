## 개요
### 구축 환경
- Host : 윈도우11이며 Docker Desktop 으로 docker 사용중
- VM : Virtual Box 사용하며 Ubuntu를 vagrant 활용하여 구성(마스터 1, 워크 2대 총 3대로 구성)
- IP
    - k8s-1(master) : 172.18.8.101
    - k8s-2(worker) : 172.18.8.102
    - k8s-3(worker) : 172.18.8.103
### 배경 설명
- 현재 로컬의 쿠버네티스 클러스터 환경에서 로컬의 docker image를 가지고 pod를 생성하기 위하여 쿠버네티스 클러스트 내부에 Docker Registry 구축 후 로컬(Window)에서 해당 Registry 에 push 후 사용하기 위함

## 순서
### 1. 마스터 노드에서 레지스트리 Deployment 만들기
- registry.yaml 을 생성한다.
  - registry 및 NodePort 를 활용하여 외부에서 접속 가능하도록 수정
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: registry
  labels:
    app: registry
spec:
  replicas: 1
  selector:
    matchLabels:
      app: registry
  template:
    metadata:
      labels:
        app: registry
    spec:
      containers:
      - name: registry
        image: registry:2
        ports:
        - containerPort: 5000
---
apiVersion: v1
kind: Service
metadata:
  name: registry
spec:
  selector:
    app: registry
  type: NodePort
  ports:
    - port: 5000
      targetPort: 5000
      nodePort: 32000  # 원하는 포트
```
- 생성 후 `kubectl apply -f registry.yaml` 명령으로 Deployment 생성

### 2. 윈도우 환경에서 build 및 push 테스트 하기
- Docker Desktop 에서 설정을 변경하여 클러스터 내부 레지스트리에 http로 접근 가능하게 하기
  - 미적용 시 https 로 접근하여 push를 실패하게 된다.
  - Docker Desktop 실행 -> 설정으로 이동 -> Docker Engine 으로 이동
  - 내부의 내용에 "insecure-registries": ["172.18.8.101:32000"] 내용을 추가한다.
    - IP 및 포트는 현재 상황에 맞게 수정
  ```json
  {
    // 기존 내용에 , 만 추가 후 아래 내용 붙혀넣기
    "insecure-registries": ["172.18.8.101:32000"]
  }
  ```
  - Apply & restart 버튼을 눌려서 적용되도록 한다
- build 하려는 프로젝트 내부에서 `docker build -t 172.18.8.101:32000/consumer:1.0.0 .` 해당 명령어로 build 한다
  - ip, port, image name, tag 등은 상황에 맞게 변경
- `docker push 172.18.8.101:32000/consumer:1.0.0` 명령어를 통해서 push 한다
- 다시 `쿠버네티스 클러스터` 환경으로 돌아가서 `curl http://172.18.8.101:32000/v2/_catalog` 명령으로 레파지토리에 이미지가 정상적으로 들어오는지 확인한다.
  - 정상적인 경우 `{"repositories":["consumer"]}` 처럼 출력된다.
  - ip와 port은 본인이 설정한대로 입력하고 image name역시 본인이 설정한 이름이 출력된다.

### 3. 각 노드에서 http로 Local Registry 에 접근 가능하게 하기
- 현 순서는 각 노드에서 모두 진행해줘야 한다.
- 다음 명령으로 파일 생성(파일이 이미 있는경우 편집)
```bash
sudo mkdir -p /etc/containerd
sudo containerd config default > /etc/containerd/config.toml
```
- `sudo vi /etc/containerd/config.toml` 명령을 통해 편집창으로 이동
- `[plugins."io.containerd.grpc.v1.cri".registry]` 아래에 다음 내용 추가
```toml
  [plugins."io.containerd.grpc.v1.cri".registry.configs]
    [plugins."io.containerd.grpc.v1.cri".registry.configs."17.5.20.23:5000"]
      [plugins."io.containerd.grpc.v1.cri".registry.configs."17.5.20.23:5000".tls]
        ca_file = ""
        cert_file = ""
        insecure_skip_verify = true
        key_file = ""
  [plugins."io.containerd.grpc.v1.cri".registry.mirrors]
  [plugins."io.containerd.grpc.v1.cri".registry.mirrors."172.18.8.101:32000"]
    endpoint = ["http://172.18.8.101:32000"]
```
- 추가 후 저장하고 bash 창에서 `systemctl restart containerd` 명령으로 재실행
- `sudo containerd config dump` 명령을 실행하여 출력되는 내용을 확인하여 제대로 적용되었는지 확인