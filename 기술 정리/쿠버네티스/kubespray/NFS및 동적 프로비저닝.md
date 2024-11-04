## 순서
### 1. 외부 서버에 nfs-utils 설치
- 현재 구축상황과 일치 시키기 위해 쿠버네티스 클러스터 외부에 Centos7 서버 구축
- 다음 명령어를 통해 nfs-utils 설치
```bash
sudo yum install -y nfs-utils
```

### 2. 공유 폴더 생싱 및 권한 설정
- 다음 명령어로 고유 폴더 생성 및 권한 설정
```bash
sudo mkdir -p /mnt/nfs_share
sudo chown -R nobody:nobody /mnt/nfs_share
sudo chmod 777 /mnt/nfs_share
```

### 3. NFS 공유 디렉토리 설정을 위한 /etc/exports 파일에 설정 추가
```bash
sudo bash -c 'echo "/mnt/nfs_share 172.18.8.0/24(rw,sync,no_subtree_check,no_root_squash)" >> /etc/exports'
```
- IP정보(예: `172.18.8.0`)는 상황에 맞게 바꿔주기

### 4. NFS 관련 시작 및 설정 및 공유 디렉토리 내보내기
- 아래 명령어를 통해 실행시 nfs-server를 자동 실행하고, 지금 즉시 nfs-server를 즉시 실행합니다.
```bash
sudo systemctl enable nfs-server 
sudo systemctl start nfs-server
```
- 아래 명령어를 통해 해당 공유 폴더를 내보냅니다.
```bash
sudo exportfs -rav
```
- 여기서 각 옵션은 아래와 같은 역할을 합니다.
    1. -r: /etc/exports 파일을 다시 읽어들여 변경 사항을 반영합니다. 이를 통해 새로운 설정을 적용할 수 있습니다.
    2. -a: /etc/exports 파일에 정의된 모든 디렉토리를 내보냅니다.
    3. -v: 내보내기 작업의 자세한 정보를 출력합니다(Verbose).

### 5. 클라우드 클러스터 환경에서도 접속을 위한 NFS 설치
- 로키에서의 설치
```bash
sudo dnf install -y nfs-utils
```
- 우분투에서의 설치
```bash
sudo apt update
sudo apt install -y nfs-common
```

### 6. 쿠버네티스 클러스터 환경에서 helm을 통한 동적 프로비저닝 환경 구축
- 아래 명령으로 쿠버네티스에 nfs-subdir-external-provisioner를 설치
```bash
helm repo add nfs-subdir-external-provisioner https://kubernetes-sigs.github.io/nfs-subdir-external-provisioner/
helm repo update
```
- (옵션)동적 프로비저닝 관련 네임스페이스 생성
    - 해당 내용은 원활한 관리를 위한것으로 굳이 따라할 필요는 없지만 앞으로 진행되는 명령어는 네임스페이스를 생성했다는 가정하에 진행됩니다.
    ```bash
    kubectl create namespace nfs-provisioner 
    ```

### 7. 프로비저너 생성
- IP는 위에서 생성한 NFS 서버의 IP로 등록
- 아래 명령어로 프로비저너를 생성한다.
```bash
helm install nfs-provisioner nfs-subdir-external-provisioner/nfs-subdir-external-provisioner \
    --namespace nfs-provisioner \
    --set nfs.server=172.18.8.4 \
    --set nfs.path=/mnt/nfs_share \
    --set storageClass.name=nfs-storage \
    --set storageClass.reclaimPolicy=Retain
```

### 8. PVC 생성
* `해당 단계부터는 테스트를 위한 단계이며 필요시 PVC를 생성하여 사용해 주세요`
- 적절한 위치에 PVC를 생성하기 위한 yaml 파일 생성
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: dynamic-nfs-pvc
spec:
  storageClassName: "nfs-storage"
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Gi
```
- 위 내용으로 파일 구성 후 저장
- 아래 명령어를 통해 pvc 생성하기
```bash
kubectl apply -f dynamic-nfs-pvc.yaml
```

### 9. 테스트 파트
- 테스트를 위한 pod 생성
    - dynamic-nfs-pod.yaml 파일 생성
    ```yaml
    apiVersion: v1
    kind: Pod
    metadata:
    name: dynamic-nfs-test-pod
    spec:
    containers:
    - name: dynamic-nfs-test-container
        image: busybox
        command: ["sleep", "3600"]
        volumeMounts:
        - name: nfs-storage
        mountPath: /mnt
    volumes:
    - name: nfs-storage
        persistentVolumeClaim:
        claimName: dynamic-nfs-pvc
    ```
    - 위 내용으로 파일 구성 후 저장
    - 아래 명령어를 통해 pod 생성하기
    ```bash
    kubectl apply -f dynamic-nfs-pod.yaml
    ```
- 생성된 pod 쉘에 진입 후 테스트 진행
```bash
kubectl exec -it dynamic-nfs-test-pod -- sh

cd /mnt
echo "Dynamic NFS Provisioner Test" > test.txt
cat test.txt
```
- 이후 NFS 서버의 공유 폴더로 이동하여 정상적으로 파일 생성 및 내용이 들어 있는지 확인하기