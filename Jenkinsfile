pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/validacao"
        DEPLOYMENT_FILE = "k8s/validacao-deployment.yaml"
        IMAGE_TAG = "latest"
    }

    triggers {
        pollSCM('* * * * *')
    }

    options {
        disableConcurrentBuilds()
    }

    stages {

        stage('Verificar Branch') {
            when {
                branch 'master'
            }
            steps {
                echo "Executando pipeline na branch master"
            }
        }

        stage('Checkout do Código') {
            steps {
                git credentialsId: 'github', // corrigido (case-sensitive)
                    url: 'https://github.com/KeyssonG/api-validacao.git',
                    branch: 'master'
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                bat """
                wsl docker build -t ${DOCKERHUB_IMAGE}:${IMAGE_TAG} .
                wsl docker tag ${DOCKERHUB_IMAGE}:${IMAGE_TAG} ${DOCKERHUB_IMAGE}:latest
                """
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    bat """
                    wsl echo %DOCKER_PASS% | wsl docker login -u %DOCKER_USER% --password-stdin
                    wsl docker push ${DOCKERHUB_IMAGE}:${IMAGE_TAG}
                    wsl docker push ${DOCKERHUB_IMAGE}:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml') {
            steps {
                bat """
                wsl sed -i 's|image:.*|image: ${DOCKERHUB_IMAGE}:${IMAGE_TAG}|g' ${DEPLOYMENT_FILE}

                wsl git config user.email "jenkins@pipeline.com"
                wsl git config user.name "Jenkins"
                wsl git add ${DEPLOYMENT_FILE}
                wsl git diff --cached --quiet || wsl git commit -m "Atualiza imagem Docker para latest"
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline concluída com sucesso! A imagem '${DOCKERHUB_IMAGE}:latest' foi atualizada e o ArgoCD aplicará as alterações automaticamente. 🚀"
        }
        failure {
            echo "Erro na pipeline. Con
