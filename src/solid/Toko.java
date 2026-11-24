package solid;

public class Toko {
    private final Keranjang keranjang = new Keranjang();
    private final StrukPrinter printer;
    private final PenyimpanRiwayat riwayat;
    private final PengirimNotifikasi notifikasi;

    public Toko(StrukPrinter printer, PenyimpanRiwayat riwayat, PengirimNotifikasi notifikasi) {
        this.printer = printer;
        this.riwayat = riwayat;
        this.notifikasi = notifikasi;
    }

    public void tambah(Produk produk) {
        keranjang.tambah(produk);
    }

    public void checkout(String pelanggan, String tujuanNotifikasi, MetodeBayar metode) {
        int total = keranjang.totalBelanja();
        int bayar = metode.totalBayar(total);

        printer.cetak(pelanggan, keranjang, metode, total, bayar);

        riwayat.simpan(pelanggan + ";" + keranjang.jumlah() + " buku;" + FormatRupiah.format(bayar));
        notifikasi.kirim(tujuanNotifikasi,
                "Terima kasih " + pelanggan + ", total bayar " + FormatRupiah.format(bayar));
    }
}
