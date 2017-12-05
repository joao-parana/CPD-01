#  cpd_kmeans - Computação Paralela e Distribuida - KMeans paralelizado com FastMPJ

## Instalando o FastMPJ da Torusware no Repositorio Maven local

É necessário Instalar o FastMPJ da Torusware no Repositorio Maven local pois o JAR não é distribuido no Maven Central.

```bash
mvn install:install-file \
    -Dfile=FastMPJ/lib/mpj.jar \
    -DgroupId=com.torusware \
    -DartifactId=fastmpj \
    -Dversion=1.0.7 \
    -Dpackaging=jar
```

Para usar este JAR especifique no arquivo `pom.xml` o texto abaixo

```xml
<dependency>
    <groupId>com.torusware</groupId>
    <artifactId>fastmpj</artifactId>
    <version>1.0.7</version>
</dependency>
```

Isto já foi feito neste projeto. Veja [o arquivo pom.xml](pom.xml)

## Fazendo o Build do projeto

```bash
mvn package
mvn assembly:single
```

## Executando o programa

É necessário configurar as variáveis de ambiente.

```bash
export FMPJ_HOME=$PWD/FastMPJ
export PATH=$FMPJ_HOME/bin:$PATH
export JAVA_HOME=/path/to/your/java # no meu caso /Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home
```

```bash
fmpjrun -np 4 -Xms512M -Xmx3000M -jar target/cpd-kmeans-1.0.0-jar-with-dependencies.jar 7
```

## Dataset

o Schema do Dataset é o seguinte:

```text
timestamp, AccelerationX, AccelerationY, AccelerationZ, MagneticFieldX, MagneticFieldY, MagneticFieldZ, Z-AxisAgle(Azimuth), X-AxisAngle(Pitch), Y-AxisAngle(Roll), GyroX, GyroY, GyroZ
```

O nome do arquivo é `measure1_smartwatch_sens_6mi.csv` e ele possui `6.382.111` tuplas com um total de `6.609.717.442` bytes

Como é muito grande foi necessário compactar e depois fazer `split` para adicionar ao GitHub.

Para descompactar faça:

```bash
cat dataset-targz-aa dataset-targz-ab dataset-targz-ac > measure1_smartwatch_sens_6mi.csv.tar.gz
tar -xzvf measure1_smartwatch_sens_6mi.csv.tar.gz
```

