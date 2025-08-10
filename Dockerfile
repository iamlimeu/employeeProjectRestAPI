FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
RUN addgroup --system myapp && \
    adduser --system --ingroup myapp myapp

WORKDIR /app
COPY target/*.jar app.jar
RUN chown -R myapp:myapp /app
USER myapp

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]


