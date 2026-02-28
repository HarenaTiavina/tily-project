package tily.mg.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entité pour stocker les détails de formation (fiofanana) d'un Mpiandraikitra
 * Sections A, B, C pour les types fanomababa et fanaterana
 */
@Entity
@Table(name = "details_fiofanana")
public class DetailsFiofanana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddetailsfiofanana")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpersonne", nullable = false)
    private Personne personne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypefiofanana", nullable = false)
    private TypeFiofanana typeFiofanana;

    // Section A: Asan'ny tsirairay
    @Column(name = "asan1", length = 255)
    private String asan1;

    @Column(name = "asan2", length = 255)
    private String asan2;

    @Column(name = "asan3", length = 255)
    private String asan3;

    @Column(name = "asan4", length = 255)
    private String asan4;

    @Column(name = "asan5", length = 255)
    private String asan5;

    @Column(name = "asan6", length = 255)
    private String asan6;

    @Column(name = "asan7", length = 255)
    private String asan7;

    @Column(name = "asanfilohananome", length = 100)
    private String asanFilohaNanome;

    // Section B: Ezakin'ny mpiofana
    @Column(name = "ezaka1", length = 255)
    private String ezaka1;

    @Column(name = "ezaka2", length = 255)
    private String ezaka2;

    @Column(name = "ezaka3", length = 255)
    private String ezaka3;

    @Column(name = "ezaka4", length = 255)
    private String ezaka4;

    @Column(name = "ezaka5", length = 255)
    private String ezaka5;

    @Column(name = "ezaka6", length = 255)
    private String ezaka6;

    @Column(name = "ezaka7", length = 255)
    private String ezaka7;

    @Column(name = "ezakafilohananome", length = 100)
    private String ezakaFilohaNanome;

    // Section C: Bitsiky ny fivondronana
    @Column(name = "bitsikydaty1")
    private LocalDate bitsikyDaty1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bitsikyfivondronana1")
    private Fivondronana bitsikyFivondronana1;

    @Column(name = "bitsikydaty2")
    private LocalDate bitsikyDaty2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bitsikyfivondronana2")
    private Fivondronana bitsikyFivondronana2;

    // Section D: Diniky ny filoha (5 thèmes et 5 filoha)
    @Column(name = "dinikytheme1", length = 255)
    private String dinikyTheme1;

    @Column(name = "dinikyfiloha1", length = 100)
    private String dinikyFiloha1;

    @Column(name = "dinikytheme2", length = 255)
    private String dinikyTheme2;

    @Column(name = "dinikyfiloha2", length = 100)
    private String dinikyFiloha2;

    @Column(name = "dinikytheme3", length = 255)
    private String dinikyTheme3;

    @Column(name = "dinikyfiloha3", length = 100)
    private String dinikyFiloha3;

    @Column(name = "dinikytheme4", length = 255)
    private String dinikyTheme4;

    @Column(name = "dinikyfiloha4", length = 100)
    private String dinikyFiloha4;

    @Column(name = "dinikytheme5", length = 255)
    private String dinikyTheme5;

    @Column(name = "dinikyfiloha5", length = 100)
    private String dinikyFiloha5;

    // Section E: Filasiana
    @Column(name = "filasianadaty")
    private LocalDate filasianaDaty;

    @Column(name = "filasianafiloha", length = 100)
    private String filasianaFiloha;

    // Section F: Ravinala (ID 3)
    @Column(name = "lasyravinala", length = 255)
    private String lasyRavinala;

    @Column(name = "soutenance", length = 255)
    private String soutenance;

    // Section G: TP2 (ID 4)
    @Column(name = "lasynanoloranatp2", length = 255)
    private String lasyNanoloranaTp2;

    // Constructors
    public DetailsFiofanana() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    // Section A
    public String getAsan1() {
        return asan1;
    }

    public void setAsan1(String asan1) {
        this.asan1 = asan1;
    }

    public String getAsan2() {
        return asan2;
    }

    public void setAsan2(String asan2) {
        this.asan2 = asan2;
    }

    public String getAsan3() {
        return asan3;
    }

    public void setAsan3(String asan3) {
        this.asan3 = asan3;
    }

    public String getAsan4() {
        return asan4;
    }

    public void setAsan4(String asan4) {
        this.asan4 = asan4;
    }

    public String getAsan5() {
        return asan5;
    }

    public void setAsan5(String asan5) {
        this.asan5 = asan5;
    }

    public String getAsan6() {
        return asan6;
    }

    public void setAsan6(String asan6) {
        this.asan6 = asan6;
    }

    public String getAsan7() {
        return asan7;
    }

    public void setAsan7(String asan7) {
        this.asan7 = asan7;
    }

    public String getAsanFilohaNanome() {
        return asanFilohaNanome;
    }

    public void setAsanFilohaNanome(String asanFilohaNanome) {
        this.asanFilohaNanome = asanFilohaNanome;
    }

    // Section B
    public String getEzaka1() {
        return ezaka1;
    }

    public void setEzaka1(String ezaka1) {
        this.ezaka1 = ezaka1;
    }

    public String getEzaka2() {
        return ezaka2;
    }

    public void setEzaka2(String ezaka2) {
        this.ezaka2 = ezaka2;
    }

    public String getEzaka3() {
        return ezaka3;
    }

    public void setEzaka3(String ezaka3) {
        this.ezaka3 = ezaka3;
    }

    public String getEzaka4() {
        return ezaka4;
    }

    public void setEzaka4(String ezaka4) {
        this.ezaka4 = ezaka4;
    }

    public String getEzaka5() {
        return ezaka5;
    }

    public void setEzaka5(String ezaka5) {
        this.ezaka5 = ezaka5;
    }

    public String getEzaka6() {
        return ezaka6;
    }

    public void setEzaka6(String ezaka6) {
        this.ezaka6 = ezaka6;
    }

    public String getEzaka7() {
        return ezaka7;
    }

    public void setEzaka7(String ezaka7) {
        this.ezaka7 = ezaka7;
    }

    public String getEzakaFilohaNanome() {
        return ezakaFilohaNanome;
    }

    public void setEzakaFilohaNanome(String ezakaFilohaNanome) {
        this.ezakaFilohaNanome = ezakaFilohaNanome;
    }

    // Section C
    public LocalDate getBitsikyDaty1() {
        return bitsikyDaty1;
    }

    public void setBitsikyDaty1(LocalDate bitsikyDaty1) {
        this.bitsikyDaty1 = bitsikyDaty1;
    }

    public Fivondronana getBitsikyFivondronana1() {
        return bitsikyFivondronana1;
    }

    public void setBitsikyFivondronana1(Fivondronana bitsikyFivondronana1) {
        this.bitsikyFivondronana1 = bitsikyFivondronana1;
    }

    public LocalDate getBitsikyDaty2() {
        return bitsikyDaty2;
    }

    public void setBitsikyDaty2(LocalDate bitsikyDaty2) {
        this.bitsikyDaty2 = bitsikyDaty2;
    }

    public Fivondronana getBitsikyFivondronana2() {
        return bitsikyFivondronana2;
    }

    public void setBitsikyFivondronana2(Fivondronana bitsikyFivondronana2) {
        this.bitsikyFivondronana2 = bitsikyFivondronana2;
    }

    public TypeFiofanana getTypeFiofanana() {
        return typeFiofanana;
    }

    public void setTypeFiofanana(TypeFiofanana typeFiofanana) {
        this.typeFiofanana = typeFiofanana;
    }

    // Section D: Diniky ny filoha
    public String getDinikyTheme1() {
        return dinikyTheme1;
    }

    public void setDinikyTheme1(String dinikyTheme1) {
        this.dinikyTheme1 = dinikyTheme1;
    }

    public String getDinikyFiloha1() {
        return dinikyFiloha1;
    }

    public void setDinikyFiloha1(String dinikyFiloha1) {
        this.dinikyFiloha1 = dinikyFiloha1;
    }

    public String getDinikyTheme2() {
        return dinikyTheme2;
    }

    public void setDinikyTheme2(String dinikyTheme2) {
        this.dinikyTheme2 = dinikyTheme2;
    }

    public String getDinikyFiloha2() {
        return dinikyFiloha2;
    }

    public void setDinikyFiloha2(String dinikyFiloha2) {
        this.dinikyFiloha2 = dinikyFiloha2;
    }

    public String getDinikyTheme3() {
        return dinikyTheme3;
    }

    public void setDinikyTheme3(String dinikyTheme3) {
        this.dinikyTheme3 = dinikyTheme3;
    }

    public String getDinikyFiloha3() {
        return dinikyFiloha3;
    }

    public void setDinikyFiloha3(String dinikyFiloha3) {
        this.dinikyFiloha3 = dinikyFiloha3;
    }

    public String getDinikyTheme4() {
        return dinikyTheme4;
    }

    public void setDinikyTheme4(String dinikyTheme4) {
        this.dinikyTheme4 = dinikyTheme4;
    }

    public String getDinikyFiloha4() {
        return dinikyFiloha4;
    }

    public void setDinikyFiloha4(String dinikyFiloha4) {
        this.dinikyFiloha4 = dinikyFiloha4;
    }

    public String getDinikyTheme5() {
        return dinikyTheme5;
    }

    public void setDinikyTheme5(String dinikyTheme5) {
        this.dinikyTheme5 = dinikyTheme5;
    }

    public String getDinikyFiloha5() {
        return dinikyFiloha5;
    }

    public void setDinikyFiloha5(String dinikyFiloha5) {
        this.dinikyFiloha5 = dinikyFiloha5;
    }

    // Section E: Filasiana
    public LocalDate getFilasianaDaty() {
        return filasianaDaty;
    }

    public void setFilasianaDaty(LocalDate filasianaDaty) {
        this.filasianaDaty = filasianaDaty;
    }

    public String getFilasianaFiloha() {
        return filasianaFiloha;
    }

    public void setFilasianaFiloha(String filasianaFiloha) {
        this.filasianaFiloha = filasianaFiloha;
    }

    // Section F: Ravinala
    public String getLasyRavinala() {
        return lasyRavinala;
    }

    public void setLasyRavinala(String lasyRavinala) {
        this.lasyRavinala = lasyRavinala;
    }

    public String getSoutenance() {
        return soutenance;
    }

    public void setSoutenance(String soutenance) {
        this.soutenance = soutenance;
    }

    // Section G: TP2
    public String getLasyNanoloranaTp2() {
        return lasyNanoloranaTp2;
    }

    public void setLasyNanoloranaTp2(String lasyNanoloranaTp2) {
        this.lasyNanoloranaTp2 = lasyNanoloranaTp2;
    }
}
