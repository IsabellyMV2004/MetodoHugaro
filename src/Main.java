//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        int l, c, tracos;
        No [] matriz;
        No[] vetor;
        boolean min_tracos = false;
        carregarDadosArquivo(matriz,l,c);


        minLinha(matriz,l,c,vetor);
        somarMinLinha(matriz,l,c,vetor);
        minColuna(matriz,l,c,vetor);

        do {
            tracos = 0;
            contarZeros();
            if(tracos != l) {

                min_tracos = true;
            }
            else {

            }

        }while(!min_tracos);
    }

    private static void carregarDadosArquivo(No[] matriz, int l, int c) {
    }

    private static void minLinha(No[] matriz,int l, int c, No[] vetor) {
        int menor;
        for (int i = 0; i < l; i++) {
            menor = 9999;
            for (int j = 0; j < c; j++)
                if(matriz[i][j] < menor)
                    menor = matriz[i][j];
            vetor[i] = menor;
        }
    }

    private static void somarMinLinha(No[] matriz, int l, int c, No[] vetor) {

    }

    private static void minColuna(No[] matriz, int l, int c, No[] vetor) {

    }
}