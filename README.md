# Parcial Segundo tercio AREP

Aplicación web de microservicios para calcular la Secuencia de Tribonacci desplegada en AWS.

## Descripción del Proyecto

### Arquitectura

![Arquitectura](/assets/arquitectura.png)

El sistema está compuesto por tres capas:

1. Cliente Web (client/index.html / servido desde http://<proxy>:8090/index.html): Interfaz HTML5 con JavaScript puro (sin librerías) que permite al usuario ingresar un valor n y obtener de forma asíncrona la secuencia de Tribonacci desde el Proxy.

2. Proxy Service (proxy-service/, puerto 8090): Servicio Spring Boot que recibe las peticiones del cliente y las distribuye en round-robin entre dos instancias del Math Service. Lee las URLs de los servicios matemáticos desde variables de entorno del sistema operativo (MATH_SERVICE_1_URL, MATH_SERVICE_2_URL). Tiene CORS configurado para aceptar peticiones desde cualquier origen.

3. Math Service (math-service/, puerto 8080): Servicio Spring Boot que calcula la Secuencia de Tribonacci de forma iterativa.

### Definición de Tribonacci

```
T(0) = 0
T(1) = 0
T(2) = 1
T(n) = T(n-1) + T(n-2) + T(n-3)  para n >= 3
```

### API

**Math Service** (puerto 8080):

| Método | Endpoint | Parámetro | Descripción |
|--------|----------|-----------|-------------|
| GET | `/tribseq` | `value=n` | Calcula la secuencia T(0)..T(n) |

Ejemplo de respuesta:
```json
{
  "operation": "Secuencia de Tribonacci",
  "input": 13,
  "output": "0, 0, 1, 1, 2, 4, 7, 13, 24, 44, 81, 149, 274, 504"
}
```

**Proxy Service** (puerto 8090):

| Método | Endpoint | Parámetro | Descripción |
|--------|----------|-----------|-------------|
| GET | `/proxy/tribseq` | `value=n` | Delega al Math Service (round-robin) |
| GET | `/proxy/status` | - | Estado del proxy y URLs de backends |

## Compilar y Correr Localmente

### Requisitos

- Java 17
- Maven 3.x

### Math Service

```bash
cd math-service
mvn clean install
```

Verificar:
```bash
curl http://localhost:8080/tribseq?value=13
```
![Endpoint-math-service](/assets/Endpoint-math-service.png)

### Proxy Service

```bash
export MATH_SERVICE_1_URL=http://localhost:8080
export MATH_SERVICE_2_URL=http://localhost:8080
cd proxy-service
mvn clean install
```

Verificar:
```bash
curl http://localhost:8090/proxy/tribseq?value=13
curl http://localhost:8090/proxy/status
```
![Proxy-Service-local](/assets/Proxy-Service-local.png)

### Cliente Web

Abrir client/index.html en el navegador (o acceder a http://localhost:8090 si usa el archivo estático del proxy). Ingresar el valor de n y presionar Calcular.

![Cliente-Web-Local](/assets/Cliente-Web-Local.png)

## Despliegue en AWS

### Instancias EC2 necesarias

| Instancia | Rol | Puerto |
|-----------|-----|--------|
| EC2-Math-1 | Math Service (instancia 1) | 8080 |
| EC2-Math-2 | Math Service (instancia 2) | 8080 |
| EC2-Proxy | Proxy Service + Cliente Web | 8090 |

![Instancias EC2](/assets//intancias.png)

Asegúrese de que los **Security Groups** de AWS permitan:
- EC2-Math-1 y EC2-Math-2: TCP 8080, 22
  ![Inbound-rules](/assets//Math-Inbound-rules.png)
- EC2-Proxy: TCP 8090, 22
  ![Inbound-rules](/assets//proxy-Inbound-rules.png)

### Instalación del software en cada EC2

En **cada una** de las tres instancias, ejecutar:

```bash
sudo yum update -y
sudo yum install java-17-amazon-corretto maven git -y
```
### EC2-Math-1 y EC2-Math-2 (los mismos comandos en las dos)

```bash
git clone https://github.com/mayerllyyo/Parcial-Segundo-tercio-AREP
cd Parcial-Segundo-tercio-AREP
cd math-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > /home/ec2-user/math.log 2>&1 &
tail -f /home/ec2-user/math.log
# started on port 8080
```
![](/assets//Compilar-Correr-math-service-aws.png)

### EC2-Proxy

```bash
git clone https://github.com/mayerllyyo/Parcial-Segundo-tercio-AREP
cd Parcial-Segundo-tercio-AREP
export MATH_SERVICE_1_URL=http://98.93.169.201:8080
export MATH_SERVICE_2_URL=http://44.204.44.163:8080
cd proxy-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > /home/ec2-user/proxy.log 2>&1 &
tail -f /home/ec2-user/proxy.log
# started on port 8090
```
![](/assets//Compilar-Correr-Proxy-Service-aws)

### Verificar que todo funciona

```bash
curl http://98.93.169.201:8080/tribseq?value=13
curl http://44.204.44.163:8080/tribseq?value=13
curl http://54.145.124.149:8090/proxy/status
```
**EC2-Math-1 y EC2-Math-2**

![](/assets//Endpoint-MATH_SERVICE_1.png)
![](/assets//Endpoint-MATH_SERVICE_2.png)

**EC2-Proxy**

![](/assets//Proxy-Service-aws-postman.png)

Para acceder al cliente web desde el browser:
```
http://54.145.124.149:8090/index.html
```
![](/assets//Cliente-Web-aws.png)

## Video de funcionamiento
[Video](https://pruebacorreoescuelaingeduco-my.sharepoint.com/:v:/g/personal/mayerlly_suarez-c_mail_escuelaing_edu_co/IQB4jtU932C1TKYvaU1OSNp6AX8jxQTmUfYu5OIXqXs6FhA?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=cFCPmV)