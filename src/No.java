public class No {
    double valor;
    int cor;

    public No(double valor, int cor) {
        this.valor = valor;
        this.cor = cor;
    }

    public No() {
        this(0,0);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
