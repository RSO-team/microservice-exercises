docker build -t lgaljo/rt_basketball_exercises -f Dockerfile_with_maven_build .
docker tag lgaljo/rt_basketball_exercises lgaljo/rt_basketball_exercises:latest
docker push -a lgaljo/rt_basketball_exercises