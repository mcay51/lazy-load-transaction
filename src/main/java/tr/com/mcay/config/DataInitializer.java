package tr.com.mcay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tr.com.mcay.entity.Department;
import tr.com.mcay.entity.Employee;
import tr.com.mcay.entity.Task;
import tr.com.mcay.repository.DepartmentRepository;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final DepartmentRepository departmentRepository;
    
    @Autowired
    public DataInitializer(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    
    @Override
    public void run(String... args) {
        // Departmanları oluştur
        Department itDepartment = new Department("IT");
        Department hrDepartment = new Department("HR");
        Department financeDepartment = new Department("Finance");
        
        // IT departmanı çalışanları ve görevleri
        Employee developer1 = new Employee("Ahmet Yılmaz", "Senior Developer");
        Employee developer2 = new Employee("Mehmet Kaya", "Junior Developer");
        Employee devops = new Employee("Ayşe Demir", "DevOps Engineer");
        
        Task task1 = new Task("API Geliştirme", "REST API'leri geliştir", LocalDate.now().plusDays(10));
        Task task2 = new Task("Bug Düzeltme", "Üretim ortamındaki hataları düzelt", LocalDate.now().plusDays(5));
        Task task3 = new Task("Dokümantasyon", "API dokümantasyonunu güncelle", LocalDate.now().plusDays(15));
        Task task4 = new Task("CI/CD Pipeline", "Jenkins pipeline'ını güncelle", LocalDate.now().plusDays(7));
        Task task5 = new Task("Kod İnceleme", "PR'ları incele", LocalDate.now().plusDays(3));
        
        developer1.addTask(task1);
        developer1.addTask(task2);
        developer2.addTask(task3);
        devops.addTask(task4);
        devops.addTask(task5);
        
        itDepartment.addEmployee(developer1);
        itDepartment.addEmployee(developer2);
        itDepartment.addEmployee(devops);
        
        // HR departmanı çalışanları ve görevleri
        Employee hrManager = new Employee("Zeynep Öztürk", "HR Manager");
        Employee hrSpecialist = new Employee("Can Yıldız", "HR Specialist");
        
        Task task6 = new Task("İşe Alım", "Yeni yazılım geliştiricileri işe al", LocalDate.now().plusDays(20));
        Task task7 = new Task("Performans Değerlendirmesi", "Yıllık performans değerlendirmelerini tamamla", LocalDate.now().plusDays(30));
        
        hrManager.addTask(task6);
        hrSpecialist.addTask(task7);
        
        hrDepartment.addEmployee(hrManager);
        hrDepartment.addEmployee(hrSpecialist);
        
        // Finance departmanı çalışanları ve görevleri
        Employee accountant = new Employee("Elif Şahin", "Accountant");
        
        Task task8 = new Task("Bütçe Planlaması", "Q3 bütçesini hazırla", LocalDate.now().plusDays(15));
        
        accountant.addTask(task8);
        
        financeDepartment.addEmployee(accountant);
        
        // Veritabanına kaydet
        departmentRepository.save(itDepartment);
        departmentRepository.save(hrDepartment);
        departmentRepository.save(financeDepartment);
    }
} 