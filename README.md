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
