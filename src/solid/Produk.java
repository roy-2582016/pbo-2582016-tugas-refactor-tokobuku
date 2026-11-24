package solid;

public interface Produk {
    String getJudul();
    String getPenulis();
    int getHarga();
    int hargaAkhir();
    String keterangan();
    void masukKe(Keranjang keranjang);
}
