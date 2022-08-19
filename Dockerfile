FROM openjdk:17.0.2-slim AS build-dist

RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list
RUN apt-get update && apt-get install -y maven fontconfig libfreetype6

WORKDIR /usr/src/project
ADD . /usr/src/project/
RUN mvn clean package -DskipTests=true -T 4

FROM openjdk:17.0.2-slim AS prod
COPY --from=build-dist /usr/src/project/marketing-main/target/marketing-main-0.0.1-SNAPSHOT.jar /bin/marketing-main-0.0.1-SNAPSHOT.jar
EXPOSE 9000
ENTRYPOINT ["java", "-Xms2g", "-Xmx2g", "-XX:MetaspaceSize=256m", "-XX:MaxMetaspaceSize=256m", "-XX:ReservedCodeCacheSize=64m", "-XX:InitialCodeCacheSize=64m", "-XX:CompressedClassSpaceSize=64m", "-XX:StringTableSize=1000003", "-XX:+UseZGC", "-XX:ConcGCThreads=1", "-XX:ParallelGCThreads=2", "-XX:ZCollectionInterval=120", "-XX:ZAllocationSpikeTolerance=5", "-XX:+UnlockDiagnosticVMOptions", "-XX:-ZProactive", "-Ddebug","-jar", "/bin/marketing-main-0.0.1-SNAPSHOT.jar"]
