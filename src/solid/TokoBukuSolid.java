package solid;

public class TokoBukuSolid {
    public static void main(String[] args) {
        Toko toko = new Toko(
                new StrukPrinterKonsol(),
                new PenyimpanRiwayatFile(),
                new PengirimEmail());

        toko.tambah(new BukuCetak("Pemrograman Java", "John Doe", 120000, 750));
        toko.tambah(new BukuCetak("Algoritma Dasar", "Jane Smith", 95000, 400));
        toko.tambah(new BukuDigital("Belajar OOP", "Ani Wijaya", 80000, 12.5));
        toko.tambah(new BukuAudio("Kisah Programmer", "Budi Santoso", 60000, 240));

        toko.checkout("Sari", "sari@mail.com", new EWallet());
    }
}
