FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-1.0

# Mandatory!
ENV APP probate-orchestrator-service.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 66

COPY docker/entrypoint.sh /
COPY build/libs/$APP /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8888/health || exit 1

EXPOSE 8888

ENTRYPOINT [ "/entrypoint.sh" ]
