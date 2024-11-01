## 순서
### 1. 로컬에 Kubespray Git 가져오기
- `git clone https://github.com/kubernetes-sigs/kubespray.git` 명령을 통해서 가져오기

### 2. Vagrantfile을 통한 VM 생성
- 설치 필요 프로그램
    - VirtualBox
    - Vagrant
- kubespray 폴더 내부의 Vagrantfile 내용 중 변경 필요한 부분 변경
    - $os ||= "ubuntu2404" 으로 os 를 ubuntu 로 사용
    - $disk_size ||= "10GB" 로 기본 할당 용량 10GB로 설정
    - 설치 VM수는 디폴트 값인 3대로 그대로 진행
- 터미널 환경에서 kubespray 폴더 이동 후 `vagrant up` 명령으로 vm 설치
    - 생성되는 vm의 이름은 디폴트가 k8s-1,2,3 이다.
    - 생성되는 vm의 디폴트 ip 주소
        - 172.18.8.101
        - 172.18.8.102
        - 172.18.8.103

### 3. Kubespray 설치 전 VM 환경 세팅
- 윈도우에서는 kubespray 설치가 빡시기 때문에 마스터 노드가 될 k8s1에서 kubespray 설치 진행
- 설치 완료 후 `vagrant ssh k8s-1`으로 vm 으로 진입
- `git clone https://github.com/kubernetes-sigs/kubespray.git` 명령으로 kubespray 가져오기
- kubespray의 원할한 설치를 위해서 k8s-1이 모든 vm이 비밀번호 없이 ssh 접근 가능하도록 설정
    - `ssh-keygen -t rsa -b 4096` 명령으로 ssh 키 생성
        - enter 계속 쳐서 기본 값으로 키 생성
    - ssh-copy-id 명령으로 생성된 키 vm에 복사
    ``` bash
        # bash 에 복붙
        ssh-copy-id vagrant@172.18.8.101
        ssh-copy-id vagrant@172.18.8.102
        ssh-copy-id vagrant@172.18.8.103
    ```
    - 비밀번호 입력창이 나오면 기본 비밀번호인 vagrant 입력
    - `ssh vagrant@172.18.8.10x` 명령으로 비밀번호 없이 진입 가능한지 확인
    - apt를 통해서 필요한 프로그램들 설치
    ```bash
    sudo apt update
    sudo apt install -y python3-pip  python3-venv
    ```
    - 이후 아래 명령으로 가상 환경 진입
    ``` bash
    python3 -m venv venv
    source venv/bin/activate
    cd kubespray
    ```
    - pip로 필요 모둘 설치
    ``` bash
    pip install -r requirements.txt
    pip install ruamel.yaml
    ```
    - 아래 명령으로 hosts.yaml 및 inventory 생성
    ```bash
    cp -rfp inventory/sample inventory/mycluster
    declare -a IPS=(172.18.8.101 172.18.8.102 172.18.8.103) # 각 VM의 IP 주소
    CONFIG_FILE=inventory/mycluster/hosts.yaml python3 contrib/inventory_builder/inventory.py ${IPS[@]}
    ``` 
    - 생성된 hosts.yaml 파일의 구성(master, worker등) 변경
    ```yaml
    all:
        hosts:
            ndeclare -a IPS=(172.18.8.101 172.18.8.102 172.18.8.103)declare -a IPS=(172.18.8.101 172.18.8.102 172.18.8.103)ode1:
            ansible_host: 172.18.8.101
            ip: 172.18.8.101
            access_ip: 172.18.8.101
            node2:
            ansible_host: 172.18.8.102
            ip: 172.18.8.102
            access_ip: 172.18.8.102
            node3:
            ansible_host: 172.18.8.103
            ip: 172.18.8.103
            access_ip: 172.18.8.103
        children:
            kube_control_plane:
            hosts:
                node1:
            kube_node:
            hosts:
                node2:
                node3:
            etcd:
            hosts:
                node1:
            k8s_cluster:
            children:
                kube_control_plane:
                kube_node:
            calico_rr:
            hosts: {}
    ```
    - helm 사용 예정이므로 `inventory/mycluster/group_vars/k8s_cluster/addons.yml` 파일 설정하여 helm 자동 설치되도록 변경
        - helm_enabled: true 
        - 위처럼 값 바꿔줄것
    - `ansible-playbook -i inventory/mycluster/hosts.yaml cluster.yml -b -v` 명령으로 kubespray를 통환 쿠버네티스 클러스터 환경 구축 시작
    - 아래명령으로 kubectl 명령 사용가능 하도록 변경
    ``` bash
    mkdir -p $HOME/.kube
    sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
    sudo chown $(id -u):$(id -g) $HOME/.kube/config
    ```