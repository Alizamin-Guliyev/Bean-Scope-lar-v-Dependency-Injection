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
