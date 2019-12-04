package com.example.liberaula;

public class SpecAula {
    public String nome ;
    public String descrizione;
    public long contatore;
    public long capienza;
    public String locazione;
    public  long coda;
    public boolean accesso;
    public String idNfc;
    public String image_url;

    public SpecAula() {
    }

    public SpecAula(String nome, String descrizione, long contatore,String idNfc, long capienza, String locazione, long coda, boolean accesso, String image_url) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.contatore = contatore;
        this.capienza = capienza;
        this.locazione = locazione;
        this.coda = coda;
        this.accesso = accesso;
        this.idNfc = idNfc;
        this.image_url = image_url;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public long getContatore() {
        return contatore;
    }

    public void setContatore(long contatore) {
        this.contatore = contatore;
    }

    public long getCapienza() {
        return capienza;
    }

    public void setCapienza(long capienza) {
        this.capienza = capienza;
    }

    public String getLocazione() {
        return locazione;
    }

    public void setLocazione(String locazione) {
        this.locazione = locazione;
    }

    public long getCoda() {
        return coda;
    }

    public void setCoda(long coda) {
        this.coda = coda;
    }

    public boolean isAccesso() {
        return accesso;
    }

    public void setAccesso(boolean accesso) {
        this.accesso = accesso;
    }

    public String getIdNfc() {
        return idNfc;
    }


    public void setIdNfc(String idNfc) {
        this.idNfc = idNfc;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}