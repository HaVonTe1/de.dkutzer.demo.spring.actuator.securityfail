# Demo to show an issue with Spring Security and Spring Actuator

### Description

Even with disabled anonymous user access - actuators health endpoint is accessable without authentication if a `base-path` without a beginning `/` is defined.

###### Details 

The `WebSecurityConfig` is done in the class `SecurityConfig`

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous().disable()//Do not allow anonymous access.
            .authorizeRequests()
            .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).permitAll()//Everybody and his grandma should be allowed -- not anonymous
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")//special actuator user for all other 
            ...
            }
```

As the comments tell no anonymous access should be allowed. Even the exposed Actuator Endpoints `Health` and `Info` should be restricted to an authenticated access.

##### Configuration

The `application.yml` is:

```yaml

management:
  endpoints:
    web:
      #default base-path is "/actuator"
      base-path: actuator #this leads to faulty anonymous access
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: when_authorized
      enabled: true
``` 

Please pay attention to the `base-path` propertie.

### How to reproduce

Start with `gradle bootRun` and execute  GET request to the `health` endpoint.

```bash
curl --request GET \
  --url http://localhost:8080/actuator/health 
```

The result is: < HTTP/1.1 200 
```json
{
	"status": "UP"
}
```
So access granted for the anonymous user...which should not be allowed.


Edit the `application.yml` and delete the `base-path` propertie or set one with a beginning `/` and everthing works as intended.



```bash
curl --request GET \
  --url http://localhost:8080/actuator/health 
```

The result is: < < HTTP/1.1 401
```json
{
	"timestamp": "2018-03-15T06:57:00.117+0000",
	"status": 401,
	"error": "Unauthorized",
	"message": "Unauthorized",
	"path": "/actuator/health"
}
```

