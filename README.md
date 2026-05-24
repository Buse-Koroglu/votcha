# 🗳️ Votcha - Real-Time Voting & Event Management Platform

Votcha, kullanıcıların etkileşimli oylama etkinlikleri oluşturabildiği, gerçek zamanlı sonuçların takip edilebildiği, yüksek performanslı ve ölçeklenebilir bir anket/oylama platformudur. Modern mikroservis pratikleri, kurumsal loglama, gelişmiş önbellekleme ve K8s tabanlı altyapısı ile milyonlarca isteği sorunsuz karşılayacak şekilde tasarlanmıştır.

## ✨ Öne Çıkan Özellikler

### 🚀 Temel Fonksiyonlar
* **Kullanıcı Kayıt & Doğrulama:** E-posta aktivasyonlu kayıt sistemi. (Doğrulanmayan hesaplar 5 dakika içinde *Scheduler* ile otomatik silinir).
* **Etkinlik Yönetimi (Standard & Surprised):** Kullanıcılar tarih sınırlamalı anketler oluşturabilir. Özel **Surprised (Sürpriz)** etkinlik formatında, oylama bitene kadar etkinlik detayları (description) diğer kullanıcılardan gizlenir; süre dolduğunda otomatik olarak ifşa edilir.
* **Gerçek Zamanlı Oylama:** Oylar doğrudan **Redis** üzerinde işlenerek sıfır gecikme ile yansıtılır, arka planda asenkron olarak veritabanına eşitlenir.

### 🛡️ Güvenlik & Yetkilendirme
* **Spring Security & JWT:** Uçtan uca güvenli, stateless kimlik doğrulama.
* **Katı Rol Yönetimi:**
    * `SUPERADMIN`: Yalnızca sistemdeki kullanıcıların rollerini değiştirebilir (Promote/Demote).
    * `ADMIN`: Raporlamalara, Grafana metriklerine ve Kibana loglarına erişebilen sistem yöneticisidir.
    * `USER`: Etkinlik oluşturabilen ve oy kullanabilen standart kullanıcı.
* **Bot Koruması:** Login ekranında **Cloudflare Turnstile** entegrasyonu.

### 📊 Analitik & Gözlemlenebilirlik (Observability)
* **Zero-Load Raporlama:** Dashboard metrikleri (toplam kullanıcı, günlük kayıt, en çok oylanan etkinlikler vb.) hiçbir şekilde PostgreSQL'i yormaz. Veriler **Elasticsearch** üzerinden saniyeler içinde çekilir.
* **Custom Traceable Logging:** Uçtan uca ELK Stack entegrasyonu. Gelen her request'e atanan benzersiz `trace_id` ve custom Spring annotasyonları (@LogExecution vb.) ile her eylem izlenebilir. (ELK trafiği TLS ile şifrelenmiştir).
* **Monitoring:** Sistem metrikleri Prometheus tarafından toplanır ve Grafana dashboardlarında görselleştirilir.

## 🛠️ Teknoloji Yığını (Tech Stack)

| Kategori | Teknolojiler |
| :--- | :--- |
| **Frontend** | React.js, Tailwind CSS, Cloudflare Turnstile |
| **Backend** | Java 21, Spring Boot, Spring Security, Hibernate |
| **Veritabanı** | PostgreSQL (AWS RDS), Redis (Caching) |
| **Arama & Analitik**| Elasticsearch, Kibana, Logstash (ELK Stack - TLS Enabled) |
| **Monitoring** | Prometheus, Grafana |
| **DevOps & Altyapı**| Kubernetes (K8s), Ingress-NGINX (Reverse Proxy / TLS), Docker |

## 🏗️ Mimari Akış (Architecture Highlight)
1. **İstemci (Client):** React üzerinden gelen HTTPS istekleri Ingress-NGINX ile karşılanır ve TLS termination işlemi yapılır.
2. **Uygulama Katmanı:** Spring Boot, yetkilendirmeyi yapar. Oylama işlemleri *Write-Through/Async* stratejisiyle önce Redis'e, sonra DB'ye yazılır.
3. **Senkronizasyon:** Veritabanındaki değişiklikler (kazananların belirlenmesi, yeni etkinlikler) arka planda Elasticsearch indexlerine (`votes`, `users`, `events`) senkronize edilir.
