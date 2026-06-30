package it.siw.tornei.dto;

public class RigaClassificaDTO {

    private Long squadraId;
    private String nomeSquadra;
    private int partiteGiocate;
    private int vittorie;
    private int pareggi;
    private int sconfitte;
    private int golFatti;
    private int golSubiti;
    private int differenzaReti;
    private int punti;

    public RigaClassificaDTO() {}
    public RigaClassificaDTO(Long squadraId, String nomeSquadra) {
        this.squadraId = squadraId;
        this.nomeSquadra = nomeSquadra;
    }

    public void aggiungiPartita(int gfHome, int gaAway, boolean isHome) {
        partiteGiocate++;
        int golFatti = isHome ? gfHome : gaAway;
        int golSubiti = isHome ? gaAway : gfHome;
        this.golFatti += golFatti;
        this.golSubiti += golSubiti;
        if (golFatti > golSubiti) { vittorie++; punti += 3; }
        else if (golFatti == golSubiti) { pareggi++; punti += 1; }
        else { sconfitte++; }
        this.differenzaReti = this.golFatti - this.golSubiti;
    }

    public Long getSquadraId() { return squadraId; }
    public void setSquadraId(Long squadraId) { this.squadraId = squadraId; }
    public String getNomeSquadra() { return nomeSquadra; }
    public void setNomeSquadra(String nomeSquadra) { this.nomeSquadra = nomeSquadra; }
    public int getPartiteGiocate() { return partiteGiocate; }
    public void setPartiteGiocate(int v) { this.partiteGiocate = v; }
    public int getVittorie() { return vittorie; }
    public void setVittorie(int v) { this.vittorie = v; }
    public int getPareggi() { return pareggi; }
    public void setPareggi(int v) { this.pareggi = v; }
    public int getSconfitte() { return sconfitte; }
    public void setSconfitte(int v) { this.sconfitte = v; }
    public int getGolFatti() { return golFatti; }
    public void setGolFatti(int v) { this.golFatti = v; }
    public int getGolSubiti() { return golSubiti; }
    public void setGolSubiti(int v) { this.golSubiti = v; }
    public int getDifferenzaReti() { return differenzaReti; }
    public void setDifferenzaReti(int v) { this.differenzaReti = v; }
    public int getPunti() { return punti; }
    public void setPunti(int v) { this.punti = v; }
}
