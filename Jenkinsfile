pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/validacao"
        IMAGE_TAG = "latest"
        DEPLOYMENT_FILE = "k8s/validacao-deployment.yaml"
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
                checkout scm
            }
        }

        stage('Build da Imagem Docker') {
            steps {
                sh '''
                    docker build -t $DOCKERHUB_IMAGE:$IMAGE_TAG .
                    docker tag $DOCKERHUB_IMAGE:$IMAGE_TAG $DOCKERHUB_IMAGE:latest
                '''
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
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $DOCKERHUB_IMAGE:$IMAGE_TAG
                        docker push $DOCKERHUB_IMAGE:latest
                    '''
                }
            }
        }

        stage('Atualizar deployment.yaml (GitOps)') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'GitHub',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )
                ]) {
                    sh '''
                        git checkout master

                        git config user.email "jenkins@pipeline.com"
                        git config user.name "Jenkins"

                        git remote set-url origin https://$GIT_USER:$GIT_TOKEN@github.com/KeyssonG/api-validacao.git

                        sed -i "s|image:.*|image: $DOCKERHUB_IMAGE:$IMAGE_TAG|" $DEPLOYMENT_FILE

                        git add $DEPLOYMENT_FILE

                        if ! git diff --cached --quiet; then
                            git commit -m "Atualiza imagem Docker para latest"
                            git push origin master
                            echo "Alterações detectadas e enviadas ao repositório."
                        else
                            echo "Nenhuma alteração detectada no deployment.yaml"
                        fi
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Pipeline concluída com sucesso! Imagem atualizada e GitOps acionado via ArgoCD."
        }
        failure {
            echo "❌ Erro na pipeline. Verifique os logs."
        }
    }
}
