# Laporan Refactor Toko Buku (SOLID)

Keluaran program setelah refactor sudah dibandingkan dengan program awal dan hasilnya sama persis. Kode hasil refactor berada di package `solid` (folder `src/solid`), dan class `main` adalah `TokoBukuSolid`.

## S — Single Responsibility Principle

**Kode awal yang melanggar**
```java
class Toko {
    int hitungOngkir(Buku b) { ... }
    int hargaAkhir(Buku b) { ... }
    String keterangan(Buku b) { ... }
    int totalBayar(String metode, int totalBelanja) { ... }
    void checkout(...) { /* cetak struk, simpan riwayat, kirim email */ }
    String formatRupiah(int angka) { ... }
}
```

**Masalah.** Class `Toko` punya banyak alasan untuk berubah: aturan harga, aturan ongkir, aturan metode bayar, format struk, dan format rupiah. Perubahan di salah satunya memaksa kita mengubah class yang sama.

**Hasil refactor.** Tiap tanggung jawab dipindah ke class sendiri:
- `Keranjang`: menyimpan isi keranjang dan menghitung total belanja serta total berat.
- `StrukPrinter` / `StrukPrinterKonsol`: mencetak struk.
- `FormatRupiah`: memformat angka menjadi rupiah.
- `MetodeBayar` dan turunannya: aturan pembayaran.
- `Toko`: hanya mengatur alur checkout.

## O — Open/Closed Principle

**Kode awal yang melanggar**
```java
int hargaAkhir(Buku b) {
    switch (b.jenis) {
        case "CETAK":   return b.getHarga() + hitungOngkir(b);
        case "DIGITAL": return b.getHarga() * 80 / 100;
        case "AUDIO":   return b.getHarga() * 90 / 100;
        ...
```
dan `totalBayar(String metode, ...)` yang berisi `if (metode.equals("TUNAI")) ... else if ...`.

**Masalah.** Menambah jenis buku atau metode bayar baru berarti membuka dan mengedit `switch`/`if` di `Toko`. Kode lama ikut berisiko rusak.

**Hasil refactor.** Setiap jenis buku (`BukuCetak`, `BukuDigital`, `BukuAudio`) menghitung `hargaAkhir()` dan `keterangan()` sendiri. Setiap metode bayar (`Tunai`, `Transfer`, `EWallet`) mengimplementasikan interface `MetodeBayar`. Jenis buku atau metode bayar baru cukup ditambah sebagai class baru tanpa mengubah `Toko`.

## L — Liskov Substitution Principle

**Kode awal yang melanggar**
```java
public int getBeratGram() {
    if (!jenis.equals("CETAK")) {
        throw new UnsupportedOperationException(judul + " tidak punya berat");
    }
    return beratGram;
}
```

**Masalah.** Semua `Buku` dianggap bisa dipakai sebagai `Produk`, tetapi memanggil `getBeratGram()`, `linkUnduh()`, atau `getDurasiMenit()` pada jenis yang salah melempar exception. Subtype tidak bisa menggantikan tipe induknya dengan aman.

**Hasil refactor.** Tidak ada lagi `UnsupportedOperationException`. Method yang tidak berlaku untuk suatu jenis buku tidak ada di jenis itu. Semua method `Produk` (`hargaAkhir()`, `keterangan()`, `getHarga()`, dst.) aman dipanggil pada subclass mana pun.

## I — Interface Segregation Principle

**Kode awal yang melanggar**
```java
interface Produk {
    int getBeratGram();     // hanya masuk akal untuk buku cetak
    String linkUnduh();     // hanya masuk akal untuk buku digital & audio
    int getDurasiMenit();   // hanya masuk akal untuk buku audio
    ...
}
```

**Masalah.** Interface gemuk memaksa semua buku mengimplementasikan method yang tidak relevan.

**Hasil refactor.** Interface dipecah menjadi `Produk` (yang umum), `DapatDikirim` (`getBeratGram()`, hanya `BukuCetak`), dan `DapatDiunduh` (`linkUnduh()`, hanya `BukuDigital` dan `BukuAudio`). `getDurasiMenit()` hanya ada di `BukuAudio`. Total berat dan daftar link unduh diambil dari daftar `DapatDikirim` dan `DapatDiunduh` di `Keranjang`, sehingga tidak perlu `instanceof` maupun pengecekan jenis.

## D — Dependency Inversion Principle

**Kode awal yang melanggar**
```java
class Toko {
    EmailSender email = new EmailSender();
    FileRiwayat riwayat = new FileRiwayat();
    ...
    void checkout(...) { System.out.println(...); riwayat.simpan(...); email.kirim(...); }
}
```

**Masalah.** `Toko` (modul tingkat tinggi) membuat sendiri class konkret `EmailSender` dan `FileRiwayat` dan mencetak langsung ke `System.out`. Mengganti email dengan SMS, atau file dengan database, berarti mengubah `Toko`. Pengujian juga sulit karena dependensi tidak bisa diganti.

**Hasil refactor.** `Toko` bergantung pada tiga interface: `StrukPrinter`, `PenyimpanRiwayat`, dan `PengirimNotifikasi`. Ketiganya disuntikkan lewat constructor. Implementasinya adalah `StrukPrinterKonsol`, `PenyimpanRiwayatFile`, dan `PengirimEmail`. Hanya `main` di `TokoBukuSolid` yang memanggil `new` untuk class-class tersebut.

## Bagian yang sengaja tidak diberi abstraksi (YAGNI/KISS)

`FormatRupiah` dibiarkan sebagai class utilitas statis tanpa interface, karena aturan format rupiah tidak punya variasi dan tidak ada alasan nyata untuk menggantinya. `Keranjang` juga tidak diberi interface karena hanya ada satu implementasi dan tidak ada kebutuhan menggantinya. Menambah abstraksi di sana hanya akan menambah berkas tanpa manfaat.
