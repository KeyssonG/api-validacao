pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/validacao"
        DEPLOYMENT_FILE = "k8s\\validacao-deployment.yaml"
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
                git credentialsId: 'Github',
                    url: 'https://github.com/KeyssonG/api-validacao.git',
                    branch: 'master'
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                bat "docker build -t %DOCKERHUB_IMAGE%:%IMAGE_TAG% ."
                bat "docker tag %DOCKERHUB_IMAGE%:%IMAGE_TAG% %DOCKERHUB_IMAGE%:latest"
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                        docker push %DOCKERHUB_IMAGE%:%IMAGE_TAG%
                        docker push %DOCKERHUB_IMAGE%:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml') {
            steps {
                script {
                    def commitSuccess = false

                    bat """
                        powershell -Command "\$content = Get-Content '${DEPLOYMENT_FILE}'; \$newContent = \$content -replace 'image: .*', 'image: ${DOCKERHUB_IMAGE}:${IMAGE_TAG}'; if (-not (\$content -eq \$newContent)) { \$newContent | Set-Content '${DEPLOYMENT_FILE}' }"
                    """

                    bat """
                        git config user.email "jenkins@pipeline.com"
                        git config user.name "Jenkins"
                        git add "${DEPLOYMENT_FILE}"
                        git diff --cached --quiet || git commit -m "Atualiza imagem Docker para latest"
                    """

                    commitSuccess = bat(script: 'git diff --cached --quiet || echo "changed"', returnStdout: true).trim() == "changed"

                    if (commitSuccess) {
                        echo "Alterações no arquivo de deployment detectadas. Commit realizado."
                    } else {
                        echo "Nenhuma alteração detectada no arquivo de deployment. Não foi realizado commit."
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline concluída com sucesso! A imagem 'keyssong/validacao:latest' foi atualizada e o ArgoCD aplicará as alterações automaticamente. 🚀"
        }
        failure {
            echo "Erro na pipeline. Confira os logs para mais detalhes."
        }
    }
}