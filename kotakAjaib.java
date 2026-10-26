import java.util.Scanner;

/**
 * ============================================================
 *  PERMAINAN KOTAK AJAIB 3x3 (Magic Square)
 * ============================================================
 * Aturan:
 *   - Papan berukuran 3x3 (9 kotak).
 *   - Pemain mengisi angka 1 sampai 9, setiap angka hanya boleh dipakai SATU kali.
 *   - Pemain menang jika jumlah setiap BARIS, KOLOM, dan kedua DIAGONAL
 *     sama dengan angka ajaib (target) = 15.
 *
 * Rumus angka ajaib untuk ukuran n x n:  n * (n^2 + 1) / 2
 *   Untuk n = 3  ->  3 * (9 + 1) / 2 = 15
 *
 * Cara menjalankan:
 *   javac KotakAjaib.java
 *   java KotakAjaib
 */
public class kotakAjaib {

    // ---------------------------------------------------------
    // KONSTANTA & VARIABEL GLOBAL
    // ---------------------------------------------------------

    /** Ukuran papan (3x3). */
    private static final int UKURAN = 3;

    /** Angka ajaib: jumlah yang harus dicapai tiap baris/kolom/diagonal. */
    private static final int TARGET = UKURAN * (UKURAN * UKURAN + 1) / 2; // = 15

    /** Papan permainan. Nilai 0 berarti kotak masih kosong. */
    private static int[][] papan = new int[UKURAN][UKURAN];

    /**
     * Penanda angka yang sudah dipakai.
     * Index 1..9 dipakai (index 0 diabaikan), sehingga ukurannya 10.
     * dipakai[5] == true artinya angka 5 sudah ada di papan.
     */
    private static boolean[] dipakai = new boolean[UKURAN * UKURAN + 1];

    /** Scanner untuk membaca input dari keyboard. */
    private static Scanner sc = new Scanner(System.in);

    // ---------------------------------------------------------
    // METHOD UTAMA
    // ---------------------------------------------------------

    public static void main(String[] args) {
        String ulang;

        do {
            resetPapan();      // kosongkan papan untuk permainan baru
            mainkan();         // jalankan satu ronde permainan

            System.out.print("\nMain lagi? (y/n): ");
            ulang = sc.nextLine().trim().toLowerCase();
        } while (ulang.equals("y"));

        System.out.println("Terima kasih sudah bermain!");
        sc.close();
    }

    /**
     * Menjalankan satu ronde permainan:
     * pemain terus mengisi kotak sampai papan penuh, lalu diperiksa.
     */
    private static void mainkan() {
        // Selama masih ada kotak kosong, minta pemain mengisi
        while (!papanPenuh()) {
            tampilkanJudul();
            tampilkanPapan();

            // --- Input baris (1-3) ---
            int baris = bacaAngka("Masukkan baris (1-3) : ", 1, UKURAN,
                                  "Baris tidak valid!");
            // --- Input kolom (1-3) ---
            int kolom = bacaAngka("Masukkan kolom (1-3) : ", 1, UKURAN,
                                  "Kolom tidak valid!");
            // --- Input angka (1-9) ---
            int angka = bacaAngka("Masukkan angka (1-9) : ", 1, UKURAN * UKURAN,
                                  "Angka harus 1 sampai 9!");

            // Konversi ke index array (mulai dari 0)
            int b = baris - 1;
            int k = kolom - 1;

            // Jika angka sudah dipakai di kotak LAIN, tolak.
            // (Jika angka sama dengan isi kotak yang sedang dipilih, itu tidak masalah.)
            if (dipakai[angka] && papan[b][k] != angka) {
                System.out.println("Angka itu sudah dipakai di kotak lain!");
                tungguEnter();
                continue;
            }

            // Jika kotak sudah berisi angka lama, bebaskan angka lama itu
            // agar bisa dipakai lagi (pemain boleh menimpa isi kotak).
            if (papan[b][k] != 0) {
                dipakai[papan[b][k]] = false;
            }

            // Isi kotak dengan angka baru dan tandai sebagai terpakai
            papan[b][k] = angka;
            dipakai[angka] = true;
        }

        // Papan sudah penuh -> tampilkan papan akhir & periksa hasilnya
        tampilkanJudul();
        tampilkanPapan();
        periksaHasil();
    }

    // ---------------------------------------------------------
    // METHOD PEMERIKSAAN
    // ---------------------------------------------------------

    /**
     * Memeriksa semua baris, kolom, dan diagonal, lalu menampilkan
     * hasilnya satu per satu beserta kesimpulan menang/kalah.
     */
    private static void periksaHasil() {
        boolean semuaBenar = true; // akan menjadi false jika ada satu saja yang salah

        System.out.println("Hasil pengecekan:");

        // --- Cek setiap BARIS ---
        for (int i = 0; i < UKURAN; i++) {
            int jumlah = 0;
            for (int j = 0; j < UKURAN; j++) {
                jumlah += papan[i][j];
            }
            semuaBenar &= cetakHasil("Baris " + (i + 1) + "   ", jumlah);
        }

        // --- Cek setiap KOLOM ---
        for (int j = 0; j < UKURAN; j++) {
            int jumlah = 0;
            for (int i = 0; i < UKURAN; i++) {
                jumlah += papan[i][j];
            }
            semuaBenar &= cetakHasil("Kolom " + (j + 1) + "   ", jumlah);
        }

        // --- Cek DIAGONAL utama (kiri atas -> kanan bawah) ---
        int diagonal1 = 0;
        for (int i = 0; i < UKURAN; i++) {
            diagonal1 += papan[i][i];
        }
        semuaBenar &= cetakHasil("Diagonal \\", diagonal1);

        // --- Cek DIAGONAL kedua (kanan atas -> kiri bawah) ---
        int diagonal2 = 0;
        for (int i = 0; i < UKURAN; i++) {
            diagonal2 += papan[i][UKURAN - 1 - i];
        }
        semuaBenar &= cetakHasil("Diagonal /", diagonal2);

        // --- Kesimpulan ---
        System.out.println();
        if (semuaBenar) {
            System.out.println("SELAMAT! Anda berhasil membuat KOTAK AJAIB!");
        } else {
            System.out.println("Belum berhasil, susunan ini bukan kotak ajaib.");
        }
    }

    /**
     * Mencetak satu baris hasil pengecekan dan mengembalikan true
     * jika jumlahnya sama dengan TARGET.
     *
     * @param nama   nama yang diperiksa (mis. "Baris 1")
     * @param jumlah total angka pada baris/kolom/diagonal tersebut
     * @return true jika jumlah == TARGET
     */
    private static boolean cetakHasil(String nama, int jumlah) {
        boolean benar = (jumlah == TARGET);
        System.out.printf("  %s = %2d %s%n", nama, jumlah, benar ? "[OK]" : "[X]");
        return benar;
    }

    // ---------------------------------------------------------
    // METHOD TAMPILAN
    // ---------------------------------------------------------

    /** Menampilkan judul dan aturan permainan. */
    private static void tampilkanJudul() {
        System.out.println();
        System.out.println("=== PERMAINAN KOTAK AJAIB 3x3 ===");
        System.out.println("Isi angka 1-9 (tidak boleh sama) agar setiap baris,");
        System.out.println("kolom, dan diagonal berjumlah " + TARGET + ".");
        System.out.println();
    }

    /** Menampilkan papan permainan dalam bentuk tabel. Kotak kosong tampil sebagai '.' */
    private static void tampilkanPapan() {
        System.out.println("          Kolom 1  Kolom 2  Kolom 3");
        for (int i = 0; i < UKURAN; i++) {
            System.out.print(" Baris " + (i + 1) + " |");
            for (int j = 0; j < UKURAN; j++) {
                String isi = (papan[i][j] == 0) ? "." : String.valueOf(papan[i][j]);
                System.out.printf("%5s   ", isi);
            }
            System.out.println();
        }
        System.out.println();
    }

    // ---------------------------------------------------------
    // METHOD BANTUAN (UTILITAS)
    // ---------------------------------------------------------

    /**
     * Membaca angka bulat dari keyboard dengan validasi.
     * Terus mengulang sampai pemain memasukkan angka dalam rentang [min, max].
     * Input non-angka (huruf, kosong) juga ditolak tanpa membuat program error.
     *
     * @param pesan       teks yang ditampilkan saat meminta input
     * @param min         nilai minimum yang diperbolehkan
     * @param max         nilai maksimum yang diperbolehkan
     * @param pesanSalah  pesan jika input tidak valid
     * @return angka valid yang dimasukkan pemain
     */
    private static int bacaAngka(String pesan, int min, int max, String pesanSalah) {
        while (true) {
            System.out.print(pesan);
            String input = sc.nextLine().trim();
            try {
                int nilai = Integer.parseInt(input);
                if (nilai >= min && nilai <= max) {
                    return nilai; // input valid
                }
            } catch (NumberFormatException e) {
                // input bukan angka -> jatuh ke pesan salah di bawah
            }
            System.out.println(pesanSalah);
        }
    }

    /** Menunggu pemain menekan Enter sebelum melanjutkan. */
    private static void tungguEnter() {
        System.out.print("Tekan Enter untuk lanjut...");
        sc.nextLine();
    }

    /** Mengosongkan papan dan semua penanda angka untuk ronde baru. */
    private static void resetPapan() {
        for (int i = 0; i < UKURAN; i++) {
            for (int j = 0; j < UKURAN; j++) {
                papan[i][j] = 0;
            }
        }
        for (int n = 0; n < dipakai.length; n++) {
            dipakai[n] = false;
        }
    }

    /** @return true jika tidak ada lagi kotak kosong (semua kotak terisi). */
    private static boolean papanPenuh() {
        for (int i = 0; i < UKURAN; i++) {
            for (int j = 0; j < UKURAN; j++) {
                if (papan[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}