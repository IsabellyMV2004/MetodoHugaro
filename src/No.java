public class No {
    int valor;
    int cor;

    public No(int valor, int cor) {
        this.valor = valor;
        this.cor = cor;
    }

    public No() {
        this(0,0);
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
