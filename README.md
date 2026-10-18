# demo2 — tapşırıqlar üzrə suallar və cavablar

# Tapşırıq 6 
 Hər sinif üçün 1 dəfə, cəmi 3 sətir. Bu sətirlər tətbiq açılanda çıxır, sorğular zamanı yox.

- `UserInfoController`, `UserInfoService` və `UserRepository` singleton bean-lərdir. Bu, Spring-in default scope-udur.
- Spring onları tətbiq başlayanda bir dəfə yaradır və sonra eyni obyekti bütün sorğular üçün istifadə edir.
- 10 sorğu göndərilsə də yeni obyekt yaranmır, ona görə konstruktor yenidən çağırılmır.
- `this.hashCode()` hər bean üçün bir dəfə çap olunur. Bu dəyər tətbiq işlədiyi müddətdə dəyişmir, çünki obyekt eynidir.

Yaradılma ardıcıllığı: `UserRepository` → `UserInfoService` → `UserInfoController`. Spring əvvəlcə asılılıqları yaradır, sonra onlardan asılı olan bean-ləri.

## Tapşırıq 7
** Bəli. `createUser` user-i `storage.put(id, user)` ilə Map-ə yazır,
`getUserById` isə `storage.get(id)` ilə həmin obyekti oxuyur. Ona görə 
`id`, `fullName`, `email` və `createdAt` eyni olur. 
Tətbiq yenidən başladılsa, user itəcək, çünki storage yalnız yaddaşdadır.

**`GET /all` neçə user qaytarır?
3 user, yeni yaradılan user siyahıda yoxdur. 
Səbəb: `getAllUsers()` storage-ın hamısını qaytarmır, yalnız sabit yazılmış ID-ləri oxuyur:
Düzgün variant bütün dəyərləri qaytarmaqdır:


## Tapşırıq 8 — Thread-safety: niyə adi `HashMap` və `long` sayğac təhlükəlidir?


Eyni anda gələn sorğuların sayı qədər. `UserRepository` singleton-dur, 
yəni tətbiqdə yalnız 1 obyekti var. Tomcat isə hər HTTP sorğusunu ayrı 
thread-də işlədir (default thread pool maksimum 200 thread). 
Ona görə 200-ə qədər thread eyni obyektdən, eyni `storage` və eyni sayğacdan 
paralel istifadə edə bilər.


`long` sayğac (`id++`) atomik deyil. O, 3 addımdan ibarətdir: oxu → 1 artır → geri yaz.
İki thread eyni anda `5` oxusa, ikisi də `6` yazar. Nəticədə iki user eyni ID alır və 
biri Map-də digərinin üstünə yazılır, yəni data itir. Bundan əlavə, bir thread-in yazdığı 
dəyəri digəri görməyə bilər (visibility problemi).
`HashMap` paralel `put()` zamanı daxili strukturunu (bucket-lər, resize) poza bilər. 
Nəticədə elementlər itir, `size()` səhv göstərir, `get()` `null` qaytarır və ya exception 
çıxır.
- `ConcurrentHashMap` paralel oxuma və yazmanı təhlükəsiz edir.
- `AtomicLong.incrementAndGet()` artırmanı CAS ilə tək, bölünməz addımda edir. Ona görə hər thread fərqli ID alır.

## Tapşırıq 11 — Prototype bean-i singleton-a birbaşa inject etmək

`UserAuditLog` (`@Scope("prototype")`) `UserInfoService`-in konstruktoruna 
`ObjectProvider` olmadan, birbaşa inject edildi. `createUser` hər çağırılanda 
`auditId` konsola yazılır. 3 user `POST /create` ilə yaradıldı.

 Hər 3 sorğuda eyni `auditId` çıxır, "UserAuditLog yaradildi" logu isə yalnız bir dəfə, 
 tətbiq açılanda görünür.

**Səbəb:**
- `UserInfoService` singleton-dur və onun konstruktoru yalnız bir dəfə çağırılır.
- Spring konstruktor üçün bir dənə `UserAuditLog` yaradır və onu sahəyə yazır.
- Prototype scope "hər **inject** zamanı yeni obyekt" deməkdir, "hər metod çağırışında" demək deyil.
- Singleton bir dəfə inject olunduğu üçün prototype obyekt də praktik olaraq singleton kimi işləyir.
- Bundan əlavə, eyni obyektin `action` sahəsini bütün thread-lər paralel dəyişir, bu da thread-safe deyil.

**Həll:** hər dəfə yeni obyekt almaq üçün aşağıdakılardan birini istifadə etmək lazımdır:
- `ObjectProvider<UserAuditLog>` və `getObject()`,
- `@Lookup` metodu,
- `@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)`.

## Tapşırıq 13 — Prototype bean-lərin destroy-u (`@PreDestroy`)
`UserAuditLog`-a `@PreDestroy destroy()` metodu əlavə edildi: 
`"UserAuditLog destroy: " + auditId`. Sonra tətbiq bağlandı.

`UserRepository`-nin `@PreDestroy` logu çıxır, `UserAuditLog`-unku isə çıxmır.
**Səbəb:**
- Spring prototype bean-i yaradır, asılılıqlarını inject edir, 
- `@PostConstruct`-u çağırır və obyekti tələb edənə verir. 
- Bundan sonra o obyekti heç yerdə saxlamır.
- Context bağlananda Spring yalnız özünün idarə etdiyi bean-ləri,
- yəni singleton-ları destroy edir. Prototype obyektlərin siyahısı 
- olmadığı üçün onların `@PreDestroy` metodu heç vaxt çağırılmır.
- Bu obyektlər istifadə bitəndən sonra Java Garbage Collector tərəfindən silinir.
- Prototype bean resurs tutursa (fayl, DB connection və s.), 
- onu bağlamaq bizim məsuliyyətimizdir.


## Tapşırıq 18 — @RequestScope-u @Scope("request") ilə əvəz et (proxyMode olmadan).

**Səbəb:**
-Tətbiq açılmayıb. Xətanın səbəbi: 
-RequestContext bean-i @Scope("request") ilə yaradılıb, amma proxyMode əlavə edilməyib.
- Xəta: ScopeNotActiveException
- Request scope aktiv olmayan vaxt Spring RequestContext bean-ini yaratmağa çalışır.
- Səbəb: @Scope("request") istifadə olunub, lakin proxyMode təyin edilməyib.
- Nəticə: Singleton bean-lərə injection zamanı tətbiq başladılmır.

-ScopeNotActiveException:
-Scope 'request' is not active for the current thread;
-consider defining a scoped proxy for this bean

Niyə bu düzəliş işləyir?
- @Scope("request") — hər HTTP sorğusu üçün ayrıca RequestContext instansının olmasını təmin edir.
- proxyMode = ScopedProxyMode.TARGET_CLASS — proxy yaradır ki, singleton bean-lər RequestContext-ə 
- təhlükəsiz şəkildə istinad edə bilsinlər.
- HTTP sorğusu gələndə proxy həmin sorğuya aid RequestContext instansına yönəlir.


## Tapşırıq 19 — `requestContext.getClass()` və scoped proxy

Controller-in `getUserById` metodunda `requestContext.getClass().getName()` çap edildi.
`az.iktex.coreplatform.simasignapi.component.RequestContext$$SpringCGLIB$$0`.
Bu, əsl `RequestContext` deyil, Spring-in CGLIB ilə yaratdığı **proxy** sinfidir, 
yəni `RequestContext`-in alt sinfi.

 Singleton-a request bean necə inject oluna bilir?:
- Controller singleton-dur və tətbiq açılanda bir dəfə yaradılır. O anda heç bir HTTP sorğusu yoxdur, ona görə əsl `RequestContext` obyekti də yoxdur.
- `@RequestScope` default olaraq `proxyMode = ScopedProxyMode.TARGET_CLASS` istifadə edir. Spring CGLIB ilə `RequestContext`-in alt sinfini yaradır və singleton-a bu **proxy**-ni bir dəfə inject edir.
- Proxy-nin hər metodu, məsələn `getRequestId()`, çağırılanda o, cari thread-ə bağlı HTTP sorğusunu tapır (`RequestContextHolder`). Sonra həmin sorğunun `RequestContext` obyektini götürür, yoxdursa yaradır, və çağırışı ona yönləndirir.
- Nəticədə sahə həmişə eyni proxy-dir, amma hər sorğu öz real obyektini görür.
- Prototype-da proxy default olaraq yoxdur. Ona görə orada birbaşa inject zamanı bir obyekt "ilişib qalırdı" (Tapşırıq 11) və `ObjectProvider` lazım oldu.

## Tapşırıq 24 — Session: Chrome, incognito və Postman
**Chrome nəticəsi:** 3 user, ən yenisi əvvəldə: `[Ali (1), Vali (2), Leyla (3)]`. `1` 
iki dəfə açılsa da siyahıda bir dəfə var, son baxış olduğu üçün birincidir.

**Incognito nəticəsi:** `[]`, yəni boş siyahı.
- Server ilk sorğuda sessiya yaradır və cavabda `Set-Cookie: JSESSIONID=...` göndərir. 
- Brauzer sonrakı sorğularda bu cookie-ni geri göndərir, Spring də onunla həmin sessiyanın `RecentlyViewedUsers` obyektini tapır.
- Incognito pəncərəsi adi pəncərənin cookie-lərini görmür. Ona görə `JSESSIONID` yoxdur, server yeni sessiya və yeni, boş `RecentlyViewedUsers` yaradır.

**Postman-də niyə boş gələ bilər:**
- Postman cookie-ni saxlamır və ya göndərmir. Məsələn, cookie jar söndürülüb, 
- ya da Postman-in Settings bölməsində "Send cookies" bağlıdır. Bu halda hər sorğu yeni sessiya sayılır.
- Sessiyanın vaxtı bitib. `session.timeout: 1m` olduğu üçün son sorğudan 1 dəqiqə sonra sessiya silinir.
- Tətbiq restart olunub. Sessiyalar yaddaşda saxlanılır, restartda itir.
- Chrome-un sessiyası Postman-ə keçmir. Postman öz cookie-ləri ilə ayrı client-dir, Chrome-da baxılan ID-lər Postman-də görünmür.


## Tapşırıq 25 — `RecentlyViewedUsers` request scope-da olsaydı

Siyahı heç vaxt dolmazdı və `/recent` həmişə `[]` qaytarardı.
- Request scope-da bean hər HTTP sorğu üçün yenidən yaradılır və sorğu bitəndə məhv edilir.
- `GET /1` çağırılanda ID yeni, boş siyahıya əlavə olunur. Sorğu bitəndə o siyahı da silinir.
- `GET /recent` ayrı sorğudur, ona görə öz yeni və boş siyahısını alır.
- Nəticədə "son baxılanlar" funksiyası işləmir. Məlumat sorğular arasında yaşamalıdır, ona görə istifadəçi başına bir obyekt saxlayan session scope lazımdır.
- Hər sorğuda `@PreDestroy` log-u da çıxardı, çünki bean hər sorğunun sonunda məhv olunur.

## Tapşırıq 27 — `MockUserRepository` və `NoUniqueBeanDefinitionException`

İndi `UserRepository` interface-inin iki bean-i var: `inMemoryUserRepository` və `mockUserRepository`. `UserInfoService` konstruktorda `UserRepository` istəyir. Spring tipə görə axtarır və iki uyğun bean tapır. Hansını verəcəyini bilmədiyi üçün tətbiq açılmır:

```
Parameter 0 of constructor in ...service.UserInfoService required a single bean, but 2 were found:
	- inMemoryUserRepository: defined in file [.../InMemoryUserRepository.class]
	- mockUserRepository: defined in file [.../MockUserRepository.class]
```

Bu `NoUniqueBeanDefinitionException` xətasıdır. Həll yolları: birinə `@Primary` qoymaq, inject edilən yerdə `@Qualifier("...")` yazmaq, parametr adını bean adı ilə eyni etmək və ya `@Profile` ilə yalnız birini aktiv etmək.

## Tapşırıq 32 — Spring-siz unit test və field injection

`UserInfoServiceTest` Spring konteynerini qaldırmır. Asılılıqları `Mockito.mock(...)` 
ilə yaradır və service-ə `new UserInfoService(userRepository, auditLogProvider, requestContext)` ilə özü ötürür.

**Field injection-a keçəndə test niyə işləmir:**
java
@Autowired
private UserRepository userRepository;
- Belə olanda dəyəri field-ə Spring reflection ilə qoyur. Unit testdə Spring yoxdur, ona görə bunu edən olmur.
- Asılılıqları qəbul edən konstruktor da qalmır. `new UserInfoService(mock)` artıq kompilyasiya olunmur.
- Parametrsiz `new UserInfoService()` ilə yaratsaq, `userRepository` `null` qalır və `getUserById` çağırılanda `NullPointerException` atılır.
- Field `private` olduğu üçün mock-u testdən birbaşa təyin etmək də olmur. Yalnız reflection və ya `@InjectMocks` ilə mümkündür.

**Constructor injection niyə yaxşıdır:**
- Asılılıqlar açıq görünür.
- Field-lər `final` ola bilir.
- Obyekt yarımçıq yaradıla bilmir.
- Test Spring-siz, adi Java kimi yazılır.
Ona görə service constructor injection-da saxlanıldı.

## Tapşırıq 33 — `List<UserRepository>` injection

`UserInfoService` konstruktoruna `List<UserRepository> allRepositories` parametri əlavə edildi və onun ölçüsü konsola çap olunur. Gözlənilən nəticə:

UserRepository bean sayi: 2
 - InMemoryUserRepository
 - MockUserRepository

**Niyə 2 element gəlir:**

- Parametr tək `UserRepository` olanda Spring bir bean seçməlidir. İki bean olduğu üçün `@Primary` və ya `@Qualifier` olmadan `NoUniqueBeanDefinitionException` atılır.
- Parametr `List<UserRepository>` olanda Spring seçim etmir. `UserRepository` tipində olan bütün bean-ləri siyahıya yığır və ötürür. Ona görə xəta yaranmır.
- `@Qualifier("mockUserRepository")` yalnız tək `UserRepository` parametrinə aiddir, siyahıya təsir etmir.
- Siyahının sırası `@Order` ilə idarə olunur. `@Order` olmayanda sıra bean-lərin qeydiyyat sırasıdır.
- Bu yanaşma bir interface-in bütün implementasiyalarını birlikdə işlətmək lazım olanda faydalıdır, məsələn bir neçə validator və ya bildiriş kanalı olanda.
- Eyni qayda `Map<String, UserRepository>` üçün də işləyir: açar bean adı, dəyər bean-in özü olur.
