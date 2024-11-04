## 순서
### 1. 최신 압축 파일 위치 확인 및 다운
- [k9s 다운 url](https://github.com/derailed/k9s/releases) 해당 url 에서 k9s_Linux_arm64.tar.gz 의 url 확인
- 위 url 정보로 마스터 노드에서 wget을 통한 다운로드
```bash
wget https://github.com/derailed/k9s/releases/download/v0.32.5/k9s_Linux_amd64.tar.gz
```

### 2. 압축 해제 
```bash
tar -xvf k9s_Linux_amd64.tar.gz
```

### 3. 실행 파일 이동
- 시스템 전역에서 k9s 명령어 사용 가능하도록 `/usr/local/bin` 으로 디렉토리 이동
``` bash
sudo mv k9s /usr/local/bin/
```

### 4. 정상 설치 확인
```bash
k9s version
```