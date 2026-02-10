# 🛡️ ContentShield CI/CD 구축 및 배포 마스터 가이드

이 문서는 단순히 배포하는 순서뿐만 아니라, 우리가 함께 거쳐온 **모든 복잡한 초기 설정 과정**과 **명령어**를 기록하고 있습니다. 나중에 환경을 처음부터 다시 구축해야 할 때 이 문서를 참고하세요.

---

## 🏗️ PART 1: 초기 시스템 구축 기록 (One-Time Setup)

우리가 겪었던 수많은 에러와 그것을 해결한 마법 같은 명령어들입니다.

### 1. 도커 엔진 및 자바 설치 (WSL2 Ubuntu)
우리는 GPG 키 에러와 저장소 문제를 해결하며 아래 과정을 거쳤습니다.
```bash
# GPG 키 및 저장소 등록 에러 해결 (NO_PUBKEY 7198F4B714ABFC68)
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 7198F4B714ABFC68

# Java 17 설치
sudo apt update && sudo apt install openjdk-17-jdk -y
```

### 2. Jenkins '도커 내 도커(DinD)' 환경 구축
젠킨스가 호스트 리눅스의 도커를 직접 제어할 수 있게 설정했습니다.
```bash
# 젠킨스 컨테이너 생성 (도커 소켓 공유 설정이 핵심!)
sudo docker run -d --name contentshield-jenkins \
  -p 8080:8080 -p 50000:50000 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v jenkins_home:/var/jenkins_home \
  -u root jenkins/jenkins:lts
```

### 3. 젠킨스 컨테이너 내부 도구 설치 (가장 고생했던 부분!)
젠킨스 컨테이너 안에는 도커 실행 파일과 빌드 도구가 없어 우리가 직접 넣어주었습니다.
```bash
# 1) 젠킨스 내부에 도커 CLI 설치
sudo docker exec -u root contentshield-jenkins apt-get update
sudo docker exec -u root contentshield-jenkins apt-get install -y docker.io

# 2) 도커 컴포즈(Compose) 바이너리 설치
sudo docker exec -u root contentshield-jenkins bash -c "curl -L 'https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64' -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose"

# 3) 빌드 가속기(Buildx) 설치 (에러 125 해결)
sudo docker exec -u root contentshield-jenkins bash -c "mkdir -p /usr/libexec/docker/cli-plugins && curl -L https://github.com/docker/buildx/releases/download/v0.20.0/buildx-v0.20.0.linux-amd64 -o /usr/libexec/docker/cli-plugins/docker-buildx && chmod +x /usr/libexec/docker/cli-plugins/docker-buildx"
```

### 4. 비밀 환경 변수(.env) 수동 생성
보안상 GitHub에 올리지 않은 AI API 키를 젠킨스 작업 공간에 직접 넣어주었습니다.
```bash
sudo docker exec -u root contentshield-jenkins bash -c "mkdir -p /var/jenkins_home/workspace/ContentShield-Pipeline/backend-fastapi && echo 'GROQ_API_KEY=gsk_...' > /var/jenkins_home/workspace/ContentShield-Pipeline/backend-fastapi/.env"
```

---

## 🚀 PART 2: 일상적인 배포 과정 (Daily Workflow)

설정은 끝났습니다! 이제 평소에는 아래 3단계만 하시면 됩니다.

### 1단계: 코드 수정 및 푸시 (윈도우 터미널)
```bash
git add .
git commit -m "작업 내용"
git push origin main
```

### 2단계: 젠킨스 빌드 (웹 브라우저)
1.  **[http://localhost:8080](http://localhost:8080)** 접속
2.  `ContentShield-Pipeline` 프로젝트 클릭
3.  **`Build Now`** 클릭 (초록불 확인)

### 3단계: 사이트 확인
*   **[http://localhost:3000](http://localhost:3000)**

---

## 🛠️ PART 3: 긴급 복구 매뉴얼

*   **젠킨스가 안 뜰 때**: `sudo docker start contentshield-jenkins`
*   **DB(마리아DB) 비번**: `1234` (포트 3307)
*   **포트 충돌 시**: 윈도우 터미널에서 `npm run dev` 등을 반드시 먼저 `Ctrl+C`로 종료하세요.

---
마지막 업데이트: 2026-02-10
