//-------------------
//  Zeki ÇIPLAK 
//  gitub.com/zkcplk
//-------------------
package basketci;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Basketci extends Application {

  // Oyunda kazanılan puan
  int puan = 0;
  // Oyunun süresi (saniye)
  int oyunZamani = 90;

  // Üstteki topun x koordinatı
  double xTop = 0;
  // Alttaki potanın x koordinatı
  double xPota = 0;
  // Aşağıya düşen topun x ve y koordinatları
  double xHareketli = 0;
  double yHareketli = 0;

  // Üst ve Alt hareketlilerin yönleri
  boolean yonTop = true;
  boolean yonPota = true;

  // Üstteki topun hızı
  double hizTop = 20;
  // Potanın hızı
  double hizPota = 30;
  // Aşağıya düşen topun hızı
  double hizHareketli = 15;

  // Üstteki topun en ve boy değerleri
  double topWidth = 50;
  double topHeight = 50;
  // Potanın en ve boy değerleri
  double potaWidth = 100;
  double potaHeight = 50;

  // Temel arayüz sahnesi
  Scene scene;
  double sceneWidth = 900;
  double sceneHeight = 800;

  // Tüm resim objeleri
  ImageView imgTop;
  ImageView imgPota;
  ImageView imgHareketli;
  ImageView imgBasket;

  // Tüm animasyon nesneleri
  Timeline animationTop;
  Timeline animationPota;
  Timeline animationHareketli;
  Timeline animationKalan;

  // basket olma koşullarından biri
  double basketHeight = sceneHeight - potaHeight - topHeight;

  BorderPane bp;
  StackPane spOrta;
  TextField txt;

  Label label = new Label("Basket!");
  Label bitiyor = new Label("Son 10 saniye");
  String oyuncu = "TEST";
  String puanTitle = "Basketçi 2022 | Puan: ";

  Stage anaPencere;
  Scene sceneSkor;

  // Skorları tutan map listesi
  HashMap<String, Integer> skorListesi = new HashMap<>();
  // Skor tablosu arayüzünde kullanılan GridPane
  GridPane gp2;

  @Override
  public void start(Stage primaryStage) {
    // bu metod dışından da erişilebilir olması gerekiyor
    anaPencere = primaryStage;

    // resimlerin yüklenmesi
    imgTop = new ImageView(new Image("file:images/top.png"));
    imgPota = new ImageView(new Image("file:images/pota.png"));
    imgBasket = new ImageView(new Image("file:images/basket.gif"));

    // çizgilerin üretilmesi
    Cizgi cizgiTop = new Cizgi(55, 0, -5, 0);
    Cizgi cizgiBottom = new Cizgi(0, 0, 50, 0);

    StackPane spTop = new StackPane(imgTop, cizgiTop);
    spTop.setAlignment(Pos.BASELINE_LEFT);
    StackPane spPota = new StackPane(imgPota, cizgiBottom);
    spPota.setAlignment(Pos.BASELINE_LEFT);

    spOrta = new StackPane();
    spOrta.setAlignment(Pos.TOP_LEFT);

    bp = new BorderPane();
    bp.setTop(spTop);
    bp.setCenter(spOrta);
    bp.setBottom(spPota);

    // Aşağıya düşen topla ilgili ayarlar
    imgHareketli = new ImageView(new Image("file:images/top.png"));
    imgHareketli.setTranslateX(xTop);
    imgHareketli.setVisible(false);

    // Basket olduğunda gözüken hareketli gif resminin ayarları
    imgBasket.setTranslateX(sceneWidth / 2 - 100);
    imgBasket.setTranslateY(sceneHeight / 2 - 200);
    imgBasket.setVisible(false);

    // Basket olduğunda çıkan "Basket!" yazısının ayarları
    label.setTranslateX(sceneWidth / 2 - 95);
    label.setTranslateY(sceneHeight / 2 - 300);
    label.setStyle("-fx-font-family: Tahoma; -fx-font-weight: bold; -fx-font-size: 50px; -fx-text-fill: NAVY");
    label.setVisible(false);

    // Oyunun bitmesine 10sn kala ortaya çıkan uyarı yazısının ayarları
    bitiyor.setTranslateX(sceneWidth / 2 - 64);
    bitiyor.setTranslateY(sceneHeight - 300);
    bitiyor.setStyle("-fx-font-family: Tahoma; -fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: TOMATO");
    bitiyor.setVisible(false);

    spOrta.getChildren().add(imgHareketli);
    spOrta.getChildren().add(imgBasket);
    spOrta.getChildren().add(label);
    spOrta.getChildren().add(bitiyor);

    // Aşağıya düşen top için animasyon
    animationHareketli = new Timeline(new KeyFrame(Duration.millis(hizHareketli), ev -> basketHareketli(xTop)));
    animationHareketli.setCycleCount(Timeline.INDEFINITE);

    // Üstteki top, herhangi bir klavye tuşuna basılınca aşağıya düşer
    bp.setOnKeyPressed(fi -> {
      animationHareketli.stop();
      imgHareketli.setVisible(false);
      xHareketli = xTop;
      imgHareketli.setTranslateX(xHareketli);
      yHareketli = 0;
      imgHareketli.setTranslateY(yHareketli);
      imgHareketli.setVisible(true);
      animationHareketli.play();
    });

    scene = new Scene(bp, sceneWidth, sceneHeight);
    anaPencere.setResizable(false);

    // Program başladığında ilk gözüken arayüzün ayarları (gp1)
    GridPane gp1 = new GridPane();
    gp1.setAlignment(Pos.CENTER);
    gp1.setPadding(new Insets(10, 10, 10, 10));
    gp1.setHgap(10);
    gp1.setVgap(10);
    gp1.add(new Label("Kullanıcı Adınız:"), 0, 0);
    txt = new TextField();
    txt.setText(oyuncu);
    gp1.add(txt, 1, 0);
    Button btnBasla = new Button("   Oyuna Başla!   ");
    btnBasla.setStyle("-fx-font-weight: bold");
    gp1.add(btnBasla, 1, 3);
    GridPane.setHalignment(btnBasla, HPos.RIGHT);

    Scene login = new Scene(gp1, 350, 150);
    anaPencere.setTitle("Basketçi 2022 | Oyun Kayıt");
    anaPencere.setScene(login);
    anaPencere.show();

    // İsim girme alanının hazır hale getirilmesi
    txt.requestFocus();

    // Skor listesini gösteren arayüzle ilgili ayarlar
    gp2 = new GridPane();
    gp2.setAlignment(Pos.CENTER);
    gp2.setPadding(new Insets(10, 10, 10, 10));
    gp2.setHgap(60);
    gp2.setVgap(20);

    Label lblUser = new Label("Kullanıcı Adı");
    Label lblPuan = new Label("Puan");
    lblUser.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-border-color: CRIMSON; -fx-border-width: 0 0 1 0;");
    lblPuan.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-border-color: CRIMSON; -fx-border-width: 0 0 1 0;");
    gp2.add(lblUser, 0, 0);
    gp2.add(lblPuan, 1, 0);

    sceneSkor = new Scene(gp2, 350, 600);

    // İlk gözüken arayüzde "Oyuna Başla!" butonuna basılınca...
    btnBasla.setOnAction(f -> {
      baslat();
    });

    // İlk gözüken arayüzde imleç, isim girme alanında iken klavyedeki herhangi bir tuşa basılınca...
    txt.setOnAction(fi -> {
      baslat();
    });
  }

  public void baslat() {
    // Herhangi bir isim girilmezse, default olarak bir isim belirlenir
    if (!"".equals(txt.getText())) {
      oyuncu = txt.getText();
    } else {
      oyuncu = "Zeki ÇIPLAK";
    }

    // sahne değişiminde ekranın yeniden ortalanması gerekir
    anaPencere.setScene(scene);
    anaPencere.setTitle("Basketçi 2022 | Oyuncu: " + oyuncu);
    ekraniOrtala();

    // animasyonların başlatılması
    animationTop = new Timeline(new KeyFrame(Duration.millis(hizTop), zeki -> basketTop()));
    animationTop.setCycleCount(Timeline.INDEFINITE);
    animationTop.play();

    animationPota = new Timeline(new KeyFrame(Duration.millis(hizPota), zeki -> basketPota()));
    animationPota.setCycleCount(Timeline.INDEFINITE);
    animationPota.play();

    animationKalan = new Timeline(new KeyFrame(Duration.millis(1000), zeki -> basketKalan()));
    animationKalan.setCycleCount(Timeline.INDEFINITE);
    animationKalan.play();

    // eski skorlar, skorlar.txt dosyasından okunup, map liste kaydedilir
    skorOku();

    // Oyunda klavye ile oynayabilmek için bunun yapılması şart
    bp.requestFocus();
  }

  public void basketKalan() {
    // Zaman harcanıyor
    oyunZamani--;

    // Zaman bitince gerçekleşecek olaylar
    if (oyunZamani <= 0) {
      animationKalan.stop();
      animationTop.stop();
      animationPota.stop();

      // Oyun bitti uyarısı ve kazanılan puanın gösterilmesi
      Alert uyari = new Alert(AlertType.INFORMATION);
      uyari.setTitle("Oyun Bitti");
      uyari.setHeaderText("Kazandığınız Puan: " + puan);
      uyari.show();

      // sahne değişimi ve ekranın yeniden ortalanması
      anaPencere.setScene(sceneSkor);
      anaPencere.setTitle("Basketçi 2022 | Skor Tablosu");
      ekraniOrtala();

      // yeni skorların hem arayüze hem de skorlar.txt dosyasına yazılması
      skorYaz();
    } else if (oyunZamani < 11) {
      // Oyunun bitimine 10 saniye kala uyarı yazısının çıkması
      bitiyor.setVisible(true);
      bitiyor.setText("Son " + oyunZamani + " saniye!");
      anaPencere.setTitle("Basketçi 2022");
    }
  }

  // Üstteki topun hareketi ile ilgili metod
  public void basketTop() {
    if (yonTop) {
      xTop += 10;
      if (scene.getWidth() - topWidth < xTop) {
        yonTop = false;
      }
    } else {
      xTop -= 10;
      if (0 > xTop) {
        yonTop = true;
      }
    }

    imgTop.setTranslateX(xTop);
    imgTop.setRotate(xTop % 360);
  }

  // Alttaki potanın hareketi ile ilgili metod
  public void basketPota() {
    if (yonPota) {
      xPota += 10;
      if (scene.getWidth() - potaWidth < xPota) {
        yonPota = false;
      }
    } else {
      xPota -= 10;
      if (0 > xPota) {
        yonPota = true;
      }
    }

    imgPota.setTranslateX(xPota);
  }

  // Aşağıya düşen topun hareketi ile ilgili metod
  public void basketHareketli(double konum) {
    if (scene.getHeight() - potaHeight < yHareketli) {
      animationHareketli.stop();
      imgHareketli.setVisible(false);
    } else {
      yHareketli += 10;
      imgHareketli.setTranslateY(yHareketli);
      imgHareketli.setRotate(yHareketli % 360);
    }

    basketMi();
  }

  // Basket olup olmadığını kontrol eden metod
  public void basketMi() {
    // Basket olma şartı 1
    if (yHareketli == basketHeight) {
      // Basket olma şartı 2
      if (xPota < xHareketli + topWidth / 1.5 && xHareketli < xPota + potaWidth / 1.5) {
        // Basket olunca geçici bir gif resmi ve yazı arayüzde belirir
        imgBasket.setVisible(true);
        label.setVisible(true);
        Timeline animationBasket = new Timeline(new KeyFrame(Duration.millis(1234), e -> {
          imgBasket.setVisible(false);
          label.setVisible(false);
        }));
        animationBasket.play();

        // Basket olunca 10 puan kazanılır
        puan += 10;
      } else {
        if (puan > 0) {
          // Düşen top potaya girmezse, 1 puan kaybedilir
          puan -= 1;
        }
      }
    }

    // Son 10 saniyeye gelene kadar, arayüz başlığında anlık olarak puan bilgisi gösterilir
    if (oyunZamani > 11) {
      anaPencere.setTitle(puanTitle + puan);
    }
  }

  public void ekraniOrtala() {
    // Sahne değişimlerde ekranın orta yeri arayüz boyutlarına göre yeniden hesaplanmalıdır
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    anaPencere.setX((screenBounds.getWidth() - anaPencere.getWidth()) / 2);
    anaPencere.setY((screenBounds.getHeight() - anaPencere.getHeight()) / 2);
  }

  // Skor tablosu için skorlar.txt dosyasını okuyup, skor map listini hazırlayan metod
  public void skorOku() {
    File dosya = new File("skorlar.txt");
    if (dosya.exists() && dosya.canRead()) {
      try {
        // İlgili dosya satır satır okunur
        Scanner okuyucu = new Scanner(dosya);
        while (okuyucu.hasNextLine()) {
          String data = okuyucu.nextLine();

          // Her satır boşluğa göre parçalanır, puan ve isim bilgileri oluşturulur
          String[] temp = data.split("\\s+");
          int userPuan = Integer.parseInt(temp[0].trim());
          String userAd = data.replace(userPuan + " ", "").trim();

          // Puan ve oyuncu ismi map liste eklenir
          skorListesi.put(userAd, userPuan);
        }

        // Daha önce skor tablosunda olmayan bir kullanıcı ise
        if (!skorListesi.containsKey(oyuncu)) {
          skorListesi.put(oyuncu, 0);
        }
      } catch (FileNotFoundException zeki) {
        // Dosya bulunamazsa, skor listesine 1 adet isim ve puan eklenir
        skorListesi.put("Zeki ÇIPLAK", 10);
        System.out.println("Hata: " + zeki);
      }
    }
  }

  // Oyun sonu yeni skora göre şekillenen listeyi arayüze ve skorlar.txt dosyasına yazdıran metod
  public void skorYaz() {
    // Aynı kullanıcının daha önceki puanı daha az ise, puanı güncellenir.
    if (skorListesi.containsKey(oyuncu) && skorListesi.get(oyuncu) < puan) {
      anaPencere.setTitle("Yeni REKOR!");
      skorListesi.put(oyuncu, puan);
    }

    // Skor listesinin puanlara göre büyükten küçüğe sıralanması
    Object[] zekiCiplak = skorListesi.entrySet().toArray();
    Arrays.sort(zekiCiplak, (Object o1, Object o2)
            -> ((Map.Entry<String, Integer>) o2)
                    .getValue()
                    .compareTo(((Map.Entry<String, Integer>) o1).getValue())
    );

    // Skor tablosunu arayüze yazdırma işlemi
    int len = (zekiCiplak.length >= 10 ? 10 : zekiCiplak.length);
    for (int i = 0; i < len; i++) {
      String key = ((Map.Entry<String, Integer>) zekiCiplak[i]).getKey();
      String val = ((Map.Entry<String, Integer>) zekiCiplak[i]).getValue().toString();

      Label tempLabelKey = new Label(key);
      Label tempLabelVal = new Label(val);

      // Oyunu oynayan listeye girdiyse, diğerlerinden daha belirgin olarak gösterilir
      if (key.equals(oyuncu)) {
        tempLabelKey.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: CORNFLOWERBLUE");
        tempLabelVal.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: NAVY");
      } else {
        tempLabelKey.setStyle("-fx-font-size: 16px");
        tempLabelVal.setStyle("-fx-font-size: 16px");
      }

      gp2.add(tempLabelKey, 0, i + 1);
      gp2.add(tempLabelVal, 1, i + 1);
    }

    // Skor tablosunu dosyaya yazdırma işlemi
    File dosya = new File("skorlar.txt");
    if (dosya.exists() && dosya.canWrite()) {
      try (PrintWriter yazdir = new PrintWriter(dosya)) {

        for (int i = 0; i < len; i++) {
          String key = ((Map.Entry<String, Integer>) zekiCiplak[i]).getKey();
          String val = ((Map.Entry<String, Integer>) zekiCiplak[i]).getValue().toString();
          yazdir.println(val + " " + key);
        }

      } catch (FileNotFoundException zeki) {
        System.out.println("Dosyaya yazdırma hatası: " + zeki);
      }
    } else {
      System.out.println("Skorların yazılacağı dosya yok veya o dosyaya yazdırma izniniz yok!");
    }
  }

  // Her şeyin başladığı yer!
  public static void main(String[] args) {
    launch(args);
  }
}

// Üst ve Alt çizgiler için sınıf
class Cizgi extends StackPane {
  public Cizgi(double top, double right, double bottom, double left) {
    setPadding(new Insets(top, right, bottom, left));
    Separator sep = new Separator();
    sep.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    sep.setValignment(VPos.BOTTOM);
    getChildren().add(sep);
  }
}
