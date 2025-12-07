# Warning - Acil Durum Uygulaması

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-26%2B-green.svg)](https://www.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.05.00-orange.svg)](https://developer.android.com/jetpack/compose)

## Project Overview

**Warning**, acil durumlarda hızlı bir şekilde yakınlarınıza acil durum mesajı göndermenizi sağlayan bir Android uygulamasıdır. Uygulama, kullanıcıların acil durum butonuna basarak konum bilgileriyle birlikte önceden tanımladıkları kişilere otomatik olarak mesaj göndermesine olanak tanır.

Uygulama, Firebase Authentication ile telefon numarası tabanlı kimlik doğrulama kullanır. Kullanıcılar kayıt olduktan sonra acil durum mesajı gönderebilecekleri kişileri (contact) ekleyebilir, bu kişilerle bağlantı kurarak (linked) karşılıklı acil durum bildirimleri alabilirler. Uygulama, gerçek zamanlı veri senkronizasyonu için Firebase Firestore kullanır ve offline çalışma desteği için Room veritabanı ile yerel veri saklama sağlar.

**Hedef Kullanıcı Kitlesi**: Acil durumlarda hızlı yardım çağrısı yapmak isteyen tüm kullanıcılar. Özellikle yalnız yaşayanlar, yaşlılar, kronik hastalığı olanlar veya riskli işlerde çalışanlar için tasarlanmıştır.

**Temel Senaryolar**:
- Kullanıcı kaydı ve telefon numarası ile giriş yapma
- Acil durum mesajı gönderme (konum bilgisi ile)
- Kişi ekleme ve yönetme
- Bağlantı (linked) istekleri gönderme/alma
- Profil düzenleme ve ayarlar
- Push bildirimleri alma

## Features

- **Kullanıcı Kimlik Doğrulama**
  - Firebase Authentication ile telefon numarası tabanlı giriş
  - SMS doğrulama kodu ile güvenli kayıt/giriş
  - Otomatik oturum yönetimi

- **Acil Durum Mesajı Gönderme**
  - Tek dokunuşla acil durum mesajı gönderme
  - Otomatik konum bilgisi ekleme
  - Tüm kayıtlı kişilere toplu mesaj gönderme
  - Gönderim durumu takibi (başarılı/başarısız sayıları)

- **Kişi Yönetimi**
  - Kişi ekleme, düzenleme ve silme
  - Kişiler için özel mesaj tanımlama
  - Kişileri üste sabitleme (isTop)
  - Kişi etiketleme (tag) sistemi
  - Konum paylaşımı tercihi yönetimi

- **Bağlantı (Linked) Sistemi**
  - Karşılıklı bağlantı kurma
  - Bağlantı istekleri gönderme/alma
  - Bağlantı onaylama/reddetme

- **Gerçek Zamanlı Senkronizasyon**
  - Firebase Firestore ile gerçek zamanlı veri senkronizasyonu
  - Profil, kişi ve bağlantı verilerinin anlık güncellenmesi
  - Offline destek ile yerel veri saklama (Room)

- **Push Bildirimleri**
  - Firebase Cloud Messaging (FCM) entegrasyonu
  - Acil durum bildirimleri alma
  - Bildirim izinleri yönetimi

- **Konum Servisleri**
  - Konum izinleri yönetimi
  - GPS durumu kontrolü
  - Acil durum mesajlarında otomatik konum ekleme

- **Profil Yönetimi**
  - Profil fotoğrafı yükleme/güncelleme
  - İsim ve acil durum mesajı düzenleme
  - Konum izni tercihleri

- **Ayarlar**
  - Uygulama ayarları yönetimi
  - Bildirim tercihleri

## Tech Stack

### Dil
- **Kotlin** (2.0.21) - Modern Android geliştirme için tercih edilen dil

### Mimariler
- **Clean Architecture** - Domain, Data, Presentation katmanları ile modüler yapı
- **MVVM (Model-View-ViewModel)** - UI state yönetimi için ViewModel pattern
- **Repository Pattern** - Veri kaynaklarının soyutlanması

### UI Framework
- **Jetpack Compose** (BOM: 2024.05.00) - Modern declarative UI framework
- **Material 3** - Google'ın en yeni tasarım sistemi
- **Navigation Compose** (2.9.0) - Ekranlar arası geçiş yönetimi

### Dependency Injection
- **Hilt** (2.51.1) - Dagger'ın Android için sadeleştirilmiş versiyonu
- **Hilt Navigation Compose** (1.2.0) - Compose ile Hilt entegrasyonu

### Network
- **Retrofit** (2.9.0) - REST API çağrıları için type-safe HTTP client
- **OkHttp** (4.12.0) - HTTP istekleri için güçlü client
- **Gson** (2.10.1) - JSON serialization/deserialization

### Asenkron İşlemler
- **Kotlin Coroutines** - Asenkron programlama
- **Flow** - Reactive stream API
- **suspend functions** - Coroutine tabanlı asenkron işlemler

### Local Storage
- **Room** (2.7.1) - SQLite wrapper, offline veri saklama
- **DataStore Preferences** (1.1.7) - Key-value veri saklama

### Firebase Services
- **Firebase Authentication** - Telefon numarası tabanlı kimlik doğrulama
- **Cloud Firestore** - NoSQL veritabanı, gerçek zamanlı senkronizasyon
- **Cloud Messaging (FCM)** - Push bildirimleri
- **Cloud Functions** - Backend iş mantığı (Node.js)
- **Firebase Analytics** - Kullanım analitiği

### Image Loading
- **Coil** (2.6.0) - Modern image loading library (Compose uyumlu)

### Diğer Kütüphaneler
- **Accompanist SwipeRefresh** (0.36.0) - Pull-to-refresh desteği
- **Lifecycle Runtime KTX** (2.9.0) - Lifecycle-aware bileşenler

## Architecture

Proje **Clean Architecture** prensiplerine göre üç ana katmana ayrılmıştır:

### Presentation Layer
- **Screens**: Jetpack Compose ile oluşturulmuş UI ekranları
  - `SplashScreen`, `SignInScreen`, `SignUpScreen`
  - `MainScreen`, `ProfileScreen`, `SettingsScreen`
  - `AddContactScreen`, `ContactLinkedScreen`
- **ViewModels**: UI state yönetimi ve business logic koordinasyonu
  - `AuthViewModel`, `RegistrationViewModel`, `VerificationViewModel`
  - `ProfileListenerViewModel`, `ContactListenerViewModel`
  - `EmergencyMessageViewModel`, `ContactActionsViewModel`, `LinkedActionsViewModel`
- **Theme**: Material 3 tabanlı tema yapılandırması

### Domain Layer
- **Models**: Business logic için domain modelleri
  - `Profile`, `Contact`, `Linked`
  - `EmergencyLocation`, `EmergencyMessageResponse`
- **UseCases**: Tek sorumluluk prensibi ile iş mantığı
  - `UserRegistrationUseCase`, `SendEmergencyMessageUseCase`
  - `ProfileUsecase`, `AddContactUseCase`
  - `ContactActionsUseCase`, `LinkedActionsUseCase`
  - `UpdateFCMTokenUseCase`
- **Repository Interfaces**: Veri kaynakları için soyut arayüzler
  - `ProfileRepository`, `EmergencyRepository`, `FirebaseRepository`

### Data Layer
- **Local**: Room veritabanı ve DataStore
  - **Entities**: `ProfileEntity`, `ContactEntity`, `LinkedEntity`
  - **DAOs**: `ProfileDao`, `ContactDao`, `LinkedDao`
  - **Database**: `AppDatabase` (Room database instance)
- **Remote**: Firebase ve REST API entegrasyonları
  - **Firestore Service**: `FirestoreService` - Firestore CRUD işlemleri
  - **Retrofit API**: `EmergencyApi` - Acil durum mesajı endpoint'i
  - **DTOs**: Data Transfer Objects (Firestore ve API için)
  - **Realtime Listeners**: Firestore snapshot listener'ları
- **Repository Implementations**: Domain repository arayüzlerinin implementasyonları
  - `ProfileRepositoryImpl`, `EmergencyRepositoryImpl`, `FirebaseRepositoryImpl`
- **Mappers**: Entity ↔ Domain model dönüşümleri
- **Network**: `ConnectivityObserver` - İnternet bağlantı durumu takibi

### Dependency Injection
- **AppModule**: Room, Retrofit, Repository'lerin sağlanması
- **FirebaseModule**: Firebase servislerinin sağlanması

### Veri Akışı
```
UI (Compose Screen) 
  → ViewModel 
    → UseCase 
      → Repository Interface 
        → Repository Implementation 
          → DataSource (Room/Firestore/API)
```

## Module Structure

Proje şu anda tek modüllü bir yapıdadır (`app` modülü). Tüm kod `app/src/main/java/com/example/warning/` altında organize edilmiştir:

```
app/
├── data/           # Data layer
│   ├── di/         # Dependency Injection modülleri
│   ├── local/      # Room database (entities, DAOs)
│   ├── remote/     # Firebase ve API servisleri
│   ├── repository/ # Repository implementasyonları
│   └── mapper/     # Entity-Domain dönüşümleri
├── domain/         # Domain layer
│   ├── model/      # Domain modelleri
│   ├── repository/ # Repository arayüzleri
│   └── usecase/    # Use case'ler
└── presentation/   # Presentation layer
    ├── ui/
    │   ├── screens/ # Compose ekranları
    │   └── theme/   # Tema yapılandırması
    └── viewModel/  # ViewModel'ler
```

**Not**: Gelecekte proje büyüdükçe feature-based modüler yapıya geçiş yapılabilir (örn: `:feature-auth`, `:feature-emergency`, `:core`).

## Navigation & Screens

Uygulama **Jetpack Navigation Compose** kullanarak ekranlar arası geçişleri yönetir. Navigation yapısı `MainActivity.kt` içindeki `AppNavigation()` composable'ında tanımlanmıştır.

### Navigation Graph

```
Splash Screen
    ├── (Kullanıcı giriş yapmışsa) → Main Screen
    └── (Kullanıcı giriş yapmamışsa) → Sign In Screen
            └── Sign Up Screen
                    └── (Kayıt sonrası) → Main Screen
```

### Ekranlar

1. **Splash Screen** (`splash`)
   - Uygulama açılışında gösterilen ilk ekran
   - Kullanıcı oturum durumunu kontrol eder
   - Giriş yapılmışsa Main Screen'e, yapılmamışsa Sign In Screen'e yönlendirir

2. **Sign In Screen** (`signIn`)
   - Telefon numarası ile giriş ekranı
   - Firebase Authentication ile SMS doğrulama
   - Sign Up Screen'e geçiş linki

3. **Sign Up Screen** (`signUp`)
   - Yeni kullanıcı kayıt ekranı
   - Telefon numarası, isim, ülke bilgileri
   - Kayıt sonrası otomatik giriş

4. **Main Screen** (`main`)
   - Ana ekran, acil durum butonu
   - Konum durumu göstergesi
   - Kişi sayısı gösterimi
   - Drawer menü ile diğer ekranlara erişim
   - Acil durum mesajı gönderme işlevi

5. **Profile Screen** (`profile`)
   - Kullanıcı profil bilgileri
   - Profil fotoğrafı, isim düzenleme
   - Acil durum mesajı özelleştirme

6. **Settings Screen** (`settings`)
   - Uygulama ayarları
   - Bildirim tercihleri

7. **Contact Linked Screen** (`contacts`)
   - Kişiler listesi
   - Bağlantılar (linked) listesi
   - Kişi ekleme/düzenleme/silme işlemleri

8. **Add Contact Screen** (`addContact`)
   - Yeni kişi ekleme formu
   - Telefon numarası, isim, özel mesaj girişi

## Data Layer & APIs

### Firebase Firestore

Uygulama, veri saklama ve gerçek zamanlı senkronizasyon için **Firebase Cloud Firestore** kullanır. Ana koleksiyonlar:

- **`profiles`**: Kullanıcı profil bilgileri
  - `id`, `phoneNumber`, `name`, `country`, `profilePhoto`
  - `emergencyMessage`, `locationPermission`, `fcmToken`

- **`contacts`**: Kullanıcıların eklediği kişiler
  - `id`, `ownerPhone`, `phone`, `name`, `country`
  - `specialMessage`, `isLocationSend`, `tag`, `isTop`
  - `isConfirmed`, `addingId`, `addedId`, `date`

**Realtime Listeners**: 
- `UserRealtimeSyncManager`: Profil değişikliklerini dinler
- `ContactRealtimeSyncManager`: Kişi listesi değişikliklerini dinler
- `LinkedRealtimeSyncManager`: Bağlantı listesi değişikliklerini dinler

### REST API (Firebase Functions)

Acil durum mesajı gönderme işlemi **Firebase Cloud Functions** üzerinden REST API ile yapılır.

**Base URL**: `http://10.0.2.2:5001/warning-5d457/us-central1/` (Emulator için)
- Production'da Firebase Functions URL'i kullanılmalıdır.

**Endpoints**:
- `POST /sendEmergency` - Acil durum mesajı gönderme
  - Request Body: `EmergencyRequestDto` (latitude, longitude, senderId)
  - Response: `EmergencyResponseDto` (successCount, failureCount)

**Retrofit Configuration**:
- `EmergencyApi` interface'i ile endpoint tanımları
- Gson converter ile JSON serialization
- Suspend functions ile coroutine desteği

### Network Error Handling

- Retrofit ile HTTP hata yönetimi
- Firestore exception handling
- ConnectivityObserver ile internet bağlantı durumu kontrolü

## Local Storage

### Room Database

Uygulama, offline çalışma ve hızlı veri erişimi için **Room** veritabanı kullanır. Veritabanı adı: `profile_database`

**Entities**:

1. **ProfileEntity** (`profile` tablosu)
   - Kullanıcının profil bilgilerini saklar
   - `id`, `phone`, `country`, `name`, `profilePhoto`
   - `emergencyMessage`, `locationPermission`, `fcmToken`
   - Oturum durumu kontrolü için kullanılır

2. **ContactEntity** (`contacts` tablosu)
   - Kullanıcının eklediği kişileri saklar
   - `id`, `ownerPhone`, `phone`, `name`, `country`
   - `specialMessage`, `isLocationSend`, `tag`, `isTop`
   - `isConfirmed`, `addedId`, `addingId`, `date`
   - Offline erişim için cache görevi görür

3. **LinkedEntity** (`linkeds` tablosu)
   - Karşılıklı bağlantıları saklar
   - `id`, `phone`, `country`, `name`, `profilePhoto`
   - `ownerPhone`, `date`, `isConfirmed`
   - Bağlantı isteklerini yönetir

**Database Version**: 5
- Migration stratejisi: `fallbackToDestructiveMigration(true)` (geliştirme aşamasında)

### DataStore Preferences

Basit key-value veriler için **DataStore Preferences** kullanılır (gelecekte genişletilebilir).

## Permissions

Uygulama aşağıdaki Android izinlerini kullanır:

| İzin | Açıklama |
|------|----------|
| `INTERNET` | Firebase servisleri ve REST API çağrıları için internet erişimi |
| `ACCESS_NETWORK_STATE` | İnternet bağlantı durumunu kontrol etmek için |
| `POST_NOTIFICATIONS` | Push bildirimleri göstermek için (Android 13+) |
| `ACCESS_FINE_LOCATION` | Acil durum mesajlarında konum bilgisi eklemek için |
| `ACCESS_COARSE_LOCATION` | Yaklaşık konum bilgisi için (FINE_LOCATION alternatifi) |

**Not**: Konum izinleri runtime'da kullanıcıdan istenir. İzin verilmezse acil durum mesajı konum bilgisi olmadan gönderilir.

## Getting Started

### Gereksinimler

- **Android Studio**: Hedgehog (2023.1.1) veya üzeri
- **JDK**: 21 (Java 21)
- **Android SDK**:
  - `minSdk`: 26 (Android 8.0)
  - `targetSdk`: 34 (Android 14)
  - `compileSdk`: 35
- **Gradle**: 8.9.2
- **Kotlin**: 2.0.21

### Kurulum Adımları

1. **Projeyi Klonlayın**
   ```bash
   git clone <repository-url>
   cd Warning
   ```

2. **Firebase Yapılandırması**
   - Firebase Console'dan yeni bir proje oluşturun
   - Android uygulaması ekleyin (`com.example.warning` package name ile)
   - `google-services.json` dosyasını indirin
   - Dosyayı `app/` klasörüne kopyalayın
   - **Not**: Projede zaten bir `google-services.json` dosyası var, ancak kendi Firebase projeniz için güncellemeniz gerekebilir

3. **Backend Yapılandırması (Opsiyonel)**
   - Firebase Functions'ı deploy etmek için:
     ```bash
     cd warning-backend
     npm install
     firebase deploy --only functions
     ```
   - `AppModule.kt` içindeki Retrofit base URL'ini güncelleyin:
     ```kotlin
     .baseUrl("https://YOUR-REGION-YOUR-PROJECT.cloudfunctions.net/")
     ```
   - Emulator kullanıyorsanız: `http://10.0.2.2:5001/warning-5d457/us-central1/`

4. **Projeyi Açın**
   - Android Studio'da `File > Open` ile projeyi açın
   - Gradle sync işleminin tamamlanmasını bekleyin

5. **Build ve Çalıştırma**
   - Emulator veya fiziksel cihaz bağlayın
   - `Run` butonuna basın veya `Shift+F10` tuşlarına basın

### Yapılandırma Dosyaları

- **`local.properties`**: SDK path'i (otomatik oluşturulur)
- **`google-services.json`**: Firebase yapılandırması (Firebase Console'dan indirilir)
- **`firebase.json`**: Firebase CLI yapılandırması (Functions için)

## Build Variants / Flavors

Proje şu anda sadece **debug** ve **release** build type'larına sahiptir. Flavor yapılandırması yoktur.

### Build Types

- **debug**: Geliştirme için
  - ProGuard devre dışı
  - Debugging etkin
- **release**: Production için
  - ProGuard: `isMinifyEnabled = false` (şu an devre dışı, gelecekte etkinleştirilebilir)

**Not**: Gelecekte `dev`, `staging`, `prod` gibi flavor'lar eklenebilir.

## Testing

Proje şu anda temel test yapılandırmasına sahiptir:

- **Unit Tests**: `app/src/test/java/` (JUnit 4.13.2)
- **Instrumentation Tests**: `app/src/androidTest/java/` (AndroidX Test)
- **UI Tests**: Compose UI test desteği mevcut

### Test Çalıştırma

```bash
# Unit testler
./gradlew test

# Instrumentation testler
./gradlew connectedAndroidTest

# Tüm testler
./gradlew check
```

**Not**: Test coverage henüz tam değil. Gelecekte test coverage artırılmalıdır.

## Known Issues & Limitations

1. **Konum Servisi**
   - Acil durum mesajlarında şu an sahte (fake) konum kullanılıyor
   - `SendEmergencyMessageUseCase.kt` içinde gerçek LocationProvider entegrasyonu yapılmalı

2. **Backend URL**
   - Retrofit base URL şu an emulator için yapılandırılmış
   - Production için Firebase Functions URL'i güncellenmeli (`AppModule.kt`)

3. **ProGuard**
   - Release build'de ProGuard devre dışı
   - Production'a geçmeden önce ProGuard kuralları eklenmeli

4. **Database Migration**
   - `fallbackToDestructiveMigration(true)` kullanılıyor (geliştirme aşamasında)
   - Production için proper migration stratejisi uygulanmalı

5. **Error Handling**
   - Bazı hata mesajları standardize edilmemiş
   - Network hataları için kullanıcı dostu mesajlar geliştirilmeli

6. **Test Coverage**
   - Unit test coverage düşük
   - ViewModel ve UseCase'ler için testler yazılmalı

7. **Offline Support**
   - Room database mevcut ancak offline-first stratejisi tam uygulanmamış
   - Firestore offline persistence etkinleştirilebilir

8. **Splash Screen**
   - `SplashScreen.kt` içinde TODO notu var: gerçek authentication check yapılmalı

## Future Improvements

1. **Konum Servisi Entegrasyonu**
   - Google Play Services Location API entegrasyonu
   - Gerçek zamanlı konum takibi
   - Konum geçmişi saklama

2. **Offline-First Architecture**
   - Firestore offline persistence etkinleştirme
   - Conflict resolution stratejisi
   - Sync status göstergesi

3. **Test Coverage Artırma**
   - ViewModel unit testleri
   - UseCase testleri
   - Repository testleri (Mock)
   - UI testleri (Compose Testing)

4. **Error Handling İyileştirmeleri**
   - Centralized error handling
   - User-friendly error messages
   - Retry mekanizmaları

5. **Performance Optimizations**
   - Image caching stratejisi
   - Lazy loading for lists
   - Database query optimizations

6. **Security Enhancements**
   - ProGuard/R8 rules
   - Certificate pinning (OkHttp)
   - Sensitive data encryption

7. **Analytics & Monitoring**
   - Firebase Crashlytics entegrasyonu
   - Custom event tracking
   - Performance monitoring

8. **UI/UX Improvements**
   - Dark mode desteği
   - Accessibility improvements
   - Animations and transitions

9. **Modularization**
   - Feature-based modules
   - Core module extraction
   - Shared resources module

10. **CI/CD Pipeline**
    - GitHub Actions veya GitLab CI
    - Automated testing
    - Automated deployment

---

## Lisans

Bu proje özel bir projedir. Lisans bilgisi için proje sahibiyle iletişime geçin.

## İletişim

Sorularınız veya önerileriniz için issue açabilirsiniz.

---

**Not**: Bu README, projenin mevcut durumunu yansıtmaktadır. Geliştirme sürecinde güncellenebilir.

