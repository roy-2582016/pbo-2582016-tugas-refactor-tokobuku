package solid;

public class PenyimpanRiwayatFile implements PenyimpanRiwayat {
    @Override
    public void simpan(String baris) {
        System.out.println("[FILE riwayat.txt] " + baris);
    }
}
