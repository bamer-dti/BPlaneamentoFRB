package pojos;

/**
 * by miguel.silva on 09-02-2017.
 */
@SuppressWarnings("unused")
public class Machina {
    private String seccao;
    private String codigo;
    private String estado;
    private String nome;
    private int ordem;

    public Machina() {

    }

    @Override
    public String toString() {
        String texto = seccao
                + "; " + codigo
                + "; " + estado
                + "; " + nome
                + "; " + ordem;

        return texto;
    }

    public String getSeccao() {
        return seccao;
    }

    public void setSeccao(String seccao) {
        this.seccao = seccao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
