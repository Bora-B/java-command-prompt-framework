# CommandPrompt
📄 Bu dokümanın [İngilizce versiyonu](README.md) da mevcuttur.
## 🙏 Teşekkür

Bu proje, Java programlama dersi kapsamında reflection konusuna örnek olarak **[Oğuz Karan](https://github.com/oguzkaran)** tarafından anlatılan uygulamadan esinlenerek geliştirilmiştir.  
Kodların uygulaması ve geliştirilmiş hali tarafımdan yazılmış olsa da temel fikir ve mimari yaklaşım hocamın anlattığı yapıya dayanmaktadır.  

---

Annotation'larla komut satırı arayüzleri oluşturmak için geliştirilmiş, hafif ve genişletilebilir bir Java framework’üdür.  
Metotları kolayca komutlara bağlayabilir ve özelleştirilebilir bir prompt üzerinden etkileşimli olarak çalıştırabilirsiniz.

## ✨ Özellikler

- Annotation tabanlı komut tanımı (`@Command`, `@Commands`)
- Bir komut için birden fazla isim tanımlayabilme
- Geçersiz komutlar veya hatalı argümanlar için açıklayıcı hata mesajları
- Builder deseni ile özelleştirilebilir prompt ve mesajlar
- Komut metotlarını çalışma zamanında toplamak ve çalıştırmak için Java Reflection kullanır.

---

## 🚀 Başlarken

### 1. Komut sınıfınızı oluşturun
> Eğer bir metodu `@Command` ile işaretler ve `value` öğesini boş bırakırsanız komut adı varsayılan olarak metodun adı olur.  
> Yapılacak işlemleri `void` metotlar olarak yazın ve `@Command` ile işaretleyin.  
> Eğer bir metot birden fazla `@Command` ile işaretlenirse tanımlanan tüm isimlerle çalıştırılabilir.

```java
import commandprompt.annotation.Command;
import commandprompt.annotation.Commands;

public class MyCommands {

    @Commands({@Command("merhaba"), @Command("selam")})
    private void selamla() {
        System.out.println("Merhaba!");
    }

    @Command("echo")
    private void yaz(String mesaj) {
        System.out.println(mesaj);
    }

    @Command("yardim")
    private void yardim() {
        System.out.println("Kullanabileceğiniz komutlar: merhaba, echo, yardim");
    }
}

```java
import commandprompt.CommandPrompt;

public class Main {
    public static void main(String[] args) {
        CommandPrompt prompt = new CommandPrompt.Builder()
            .setPrompt("konsol")
            .setSuffix(">")
            .register(new MyCommands())
            .build();

        prompt.run();
    }
}
```
### 2. CommandPrompt'u oluşturun ve sınıfınızı kaydedin
> Aşağıdaki gibi CommandPrompt nesnenizi oluşturun ve register metoduna komutları tanımladığınız sınıftan bir nesne verin.
```java
import commandprompt.CommandPrompt;

public class Main {
    public static void main(String[] args) {
        CommandPrompt prompt = new CommandPrompt.Builder()
            .setPrompt("cli")
            .setSuffix(">")
            .register(new MyCommands())
            .build();

        prompt.run();
    }
}
```

## ⚙️ Özelleştirme

Prompt’u builder kullanarak aşağıdaki şekilde özelleştirebilirsiniz:

| Metot                                       | Açıklama                            |
|---------------------------------------------|------------------------------------|
| `setPrompt(String prompt)`                  | Prompt başını ayarlar               |
| `setSuffix(String suffix)`                  | Prompt sonunu ayarlar                 |
| `setWrongNumberOfArgumentsMessage(...)`     | Yanlış argüman sayısı mesajı      |
| `setInvalidCommandMessage(...)`             | Geçersiz komut mesajı     |
| `setParameterStringTypeErrorMessage(...)`   | Parametre tipi uyumsuzluğu mesajı      |

## 🛠 Teknik Detaylar

Bu proje Java Reflection API kullanarak:
- Kayıtlı sınıflarda `@Command` ile işaretlenmiş metotları tarar.
- Komut isimlerini ve parametre bilgilerini çıkarır.
- Girilen komut ismiyle eşleşen metodu çalışma zamanında dinamik olarak çalıştırır.

