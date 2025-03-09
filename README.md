# Lazy Loading ve N+1 Problemi Simülasyonu

Bu proje, Spring Boot ile Lazy Loading ve N+1 problemini simüle etmek ve çözmek için oluşturulmuştur. Projede, JPA/Hibernate'in lazy loading özelliği, N+1 problemi ve bu problemin çözüm yöntemleri gösterilmektedir.

## İçindekiler

- [Proje Yapısı](#proje-yapısı)
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Lazy Loading Nedir?](#lazy-loading-nedir)
- [N+1 Problemi Nedir?](#n1-problemi-nedir)
- [N+1 Problemini Çözme Yöntemleri](#n1-problemini-çözme-yöntemleri)
- [API Endpoint'leri](#api-endpointleri)
- [Projeyi Çalıştırma](#projeyi-çalıştırma)
- [H2 Konsolu](#h2-konsolu)

## Proje Yapısı

Proje, aşağıdaki katmanlardan oluşmaktadır:

- **Entity**: Veritabanı tablolarını temsil eden JPA entity sınıfları
- **Repository**: Veritabanı işlemlerini gerçekleştiren JPA repository sınıfları
- **Service**: İş mantığını içeren servis sınıfları
- **Controller**: REST API endpoint'lerini sağlayan controller sınıfları
- **DTO**: Veri transfer nesneleri

## Kullanılan Teknolojiler

- Java 17
- Spring Boot 2.7.14
- Spring Data JPA
- H2 Database (In-memory)
- Swagger/SpringFox (API Dokümantasyonu)
- Lombok

## Lazy Loading Nedir?

Lazy Loading (Tembel Yükleme), ilişkili verilerin sadece ihtiyaç duyulduğunda yüklenmesini sağlayan bir tekniktir. Bu, performansı artırmak ve gereksiz veri yüklemesini önlemek için kullanılır.

JPA/Hibernate'de, ilişkiler varsayılan olarak şu şekilde yapılandırılır:
- `@OneToMany` ve `@ManyToMany` ilişkileri: LAZY (tembel)
- `@OneToOne` ve `@ManyToOne` ilişkileri: EAGER (istekli)

Lazy loading, ilişkili verilere erişildiğinde otomatik olarak veritabanından yüklenir. Örneğin:

```java
Department department = departmentRepository.findById(1L).get();
// Bu noktada, department.getEmployees() henüz yüklenmemiştir

List<Employee> employees = department.getEmployees(); // Lazy loading tetiklenir
// Bu noktada, employees veritabanından yüklenir
```

## N+1 Problemi Nedir?

N+1 problemi, ORM (Object-Relational Mapping) kullanırken sıkça karşılaşılan bir performans sorunudur. Ana sorgu (1) ve ardından ilişkili her kayıt için ayrı sorgular (N) yapılması durumunda ortaya çıkar.

Örnek:

```java
List<Department> departments = departmentRepository.findAll(); // 1 sorgu

for (Department department : departments) {
    List<Employee> employees = department.getEmployees(); // Her department için 1 sorgu (N sorgu)
    // ...
}
```

Bu örnekte, toplam 1 + N sorgu yapılır. Eğer 100 department varsa, 101 sorgu yapılır!

### N+1 Problemi Akış Şeması

```
1. Ana Sorgu (1 sorgu)
   |
   +--> Entity A alınır
        |
        +--> Entity A'nın ilişkili Entity B'leri için lazy loading (N sorgu)
             |
             +--> Her bir Entity B için ilişkili Entity C'ler için lazy loading (N*M sorgu)
```

## N+1 Problemini Çözme Yöntemleri

### 1. Join Fetch Kullanımı

JPQL veya Criteria API ile join fetch kullanarak ilişkili verileri tek bir sorguda getirebilirsiniz:

```java
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
Optional<Department> findByIdWithEmployees(Long id);
```

Bu yöntem, tek bir SQL sorgusu ile ana entity ve ilişkili entity'leri birlikte getirir.

### 2. Batch Size Ayarı

Hibernate'in batch size özelliği, ilişkili entity'leri toplu olarak yüklemeyi sağlar:

```java
@Entity
public class Department {
    // ...
    
    @OneToMany(mappedBy = "department")
    @BatchSize(size = 30)
    private Set<Employee> employees;
}
```

Veya global olarak hibernate.default_batch_fetch_size ayarı ile:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=30
```

Bu, N+1 problemini N/batch_size + 1 problemine dönüştürür.

### 3. DTO (Data Transfer Object) Kullanımı

Sadece ihtiyaç duyulan alanları içeren DTO'lar oluşturarak, gereksiz veri yüklemesini önleyebilirsiniz:

```java
@Query("SELECT new com.example.dto.DepartmentDTO(d.id, d.name) FROM Department d WHERE d.id = :id")
Optional<DepartmentDTO> findDepartmentDTOById(Long id);
```

### 4. EntityGraph Kullanımı

JPA 2.1 ile gelen EntityGraph, hangi ilişkilerin yükleneceğini belirtmenin daha esnek bir yoludur:

```java
@EntityGraph(attributePaths = {"employees"})
@Query("SELECT d FROM Department d WHERE d.id = :id")
Optional<Department> findByIdWithEmployees(Long id);
```

## API Endpoint'leri

Swagger UI üzerinden tüm API endpoint'lerine erişilebilir:

```
http://localhost:8080/swagger-ui/
```

### Departman API'leri

#### Temel CRUD İşlemleri
- `GET /api/departments`: Tüm departmanları listeler
- `GET /api/departments/{id}`: ID'ye göre departman getirir
- `POST /api/departments`: Yeni departman oluşturur
- `PUT /api/departments/{id}`: Var olan departmanı günceller
- `DELETE /api/departments/{id}`: Departmanı siler

#### Lazy Loading ve N+1 Test API'leri
- `GET /api/departments/{id}/with-employees-transactional`: Transaction içinde lazy loading ile departman ve çalışanlarını getirir (N+1 problemi var)
- `GET /api/departments/{id}/with-employees-non-transactional`: Transaction olmadan lazy loading ile departman ve çalışanlarını getirir (LazyInitializationException)
- `GET /api/departments/{id}/with-employees-fetch`: Join fetch ile departman ve çalışanlarını getirir (N+1 problemi azaltılmış)
- `GET /api/departments/{id}/with-employees-and-tasks-fetch`: Join fetch ile departman, çalışanları ve görevlerini getirir (N+1 problemi azaltılmış)
- `GET /api/departments/{id}/with-employees-and-tasks-dto`: DTO kullanarak departman, çalışanları ve görevlerini getirir (N+1 problemi var)
- `GET /api/departments/{id}/with-employees-and-tasks-one-query`: Tek sorguda departman, çalışanları ve görevlerini getirir (N+1 problemi çözülmüş)
- `GET /api/departments/{id}/with-employees-and-tasks-one-query-dto`: Tek sorguda DTO olarak departman, çalışanları ve görevlerini getirir (N+1 problemi çözülmüş)

### Çalışan API'leri

#### Temel CRUD İşlemleri
- `GET /api/employees`: Tüm çalışanları listeler
- `GET /api/employees/{id}`: ID'ye göre çalışan getirir
- `GET /api/employees/department/{departmentId}`: Departman ID'sine göre çalışanları listeler
- `POST /api/employees`: Yeni çalışan oluşturur
- `PUT /api/employees/{id}`: Var olan çalışanı günceller
- `DELETE /api/employees/{id}`: Çalışanı siler

#### Lazy Loading ve N+1 Test API'leri
- `GET /api/employees/{id}/with-tasks-transactional`: Transaction içinde lazy loading ile çalışan ve görevlerini getirir (N+1 problemi var)
- `GET /api/employees/{id}/with-tasks-non-transactional`: Transaction olmadan lazy loading ile çalışan ve görevlerini getirir (LazyInitializationException)
- `GET /api/employees/{id}/with-tasks-fetch`: Join fetch ile çalışan ve görevlerini getirir (N+1 problemi azaltılmış)
- `GET /api/employees/{id}/with-tasks-batch-size`: Batch size kullanarak çalışan ve görevlerini getirir (N+1 problemi azaltılmış)
- `GET /api/employees/{id}/with-tasks-and-department`: Transaction içinde nested lazy loading ile çalışan, görevleri ve departmanını getirir (N+1 problemi var)
- `GET /api/employees/{id}/with-tasks-and-department-fetch`: Join fetch ile çalışan, görevleri ve departmanını getirir (N+1 problemi azaltılmış)
- `GET /api/employees/{id}/with-tasks-dto`: DTO kullanarak çalışan ve görevlerini getirir (N+1 problemi var)
- `GET /api/employees/{id}/with-tasks-one-query`: Tek sorguda çalışan ve görevlerini getirir (N+1 problemi çözülmüş)
- `GET /api/employees/{id}/with-tasks-one-query-dto`: Tek sorguda DTO olarak çalışan ve görevlerini getirir (N+1 problemi çözülmüş)

### Görev API'leri

#### Temel CRUD İşlemleri
- `GET /api/tasks`: Tüm görevleri listeler
- `GET /api/tasks/{id}`: ID'ye göre görev getirir
- `GET /api/tasks/employee/{employeeId}`: Çalışan ID'sine göre görevleri listeler
- `GET /api/tasks/overdue`: Gecikmiş görevleri listeler
- `POST /api/tasks`: Yeni görev oluşturur
- `PUT /api/tasks/{id}`: Var olan görevi günceller
- `PUT /api/tasks/{id}/complete`: Görevi tamamlandı olarak işaretler
- `DELETE /api/tasks/{id}`: Görevi siler

## N+1 Problemi Çözüm Akış Şeması

```
1. Problemi Tanımlama
   |
   +--> N+1 sorgu tespiti (SQL logları inceleme)
        |
        +--> Çözüm Yöntemleri
             |
             +--> Join Fetch Kullanımı
             |    |
             |    +--> @Query ile JPQL yazma
             |         |
             |         +--> SELECT e FROM Employee e JOIN FETCH e.tasks
             |
             +--> Batch Size Ayarı
             |    |
             |    +--> @BatchSize anotasyonu
             |    |    |
             |    |    +--> @BatchSize(size = 30)
             |    |
             |    +--> Global ayar
             |         |
             |         +--> hibernate.default_batch_fetch_size=30
             |
             +--> DTO Kullanımı
                  |
                  +--> Projection veya constructor expression
                       |
                       +--> SELECT new DepartmentDTO(d.id, d.name)
```

## Projeyi Çalıştırma

```bash
./mvnw spring-boot:run
```

Uygulama başlatıldığında, örnek veriler otomatik olarak yüklenir ve H2 veritabanına kaydedilir.

## H2 Konsolu

H2 veritabanı konsoluna aşağıdaki URL üzerinden erişilebilir:

```
http://localhost:8080/h2-console
```

Bağlantı bilgileri:
- JDBC URL: `jdbc:h2:mem:testdb`
- Kullanıcı adı: `sa`
- Şifre: (boş bırakın) 