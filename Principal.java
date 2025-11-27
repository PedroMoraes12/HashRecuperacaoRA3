package hash_rec3;

import java.util.Random;

public class Principal {

    public static void main(String[] args) {
        executarExperimento(1009, 1000, 1, 137, "AQUECIMENTO", false);

        int[] tamanhosM = {1009, 10007, 100003};
        int[] tamanhosN = {1000, 10000, 100000};
        int[] seeds = {137, 271828, 314159};
        int[] funcoes = {1, 2, 3};

        System.out.println("m,n,func,seed,ins_ms,coll_tbl,coll_lst,find_ms_hits,find_ms_misses,cmp_hits,cmp_misses,checksum");

        int iM = 0;
        while (iM < 3) { 
            int m = tamanhosM[iM];
            
            int iN = 0;
            while (iN < 3) { 
                int n = tamanhosN[iN];
                
                int iF = 0;
                while (iF < 3) { 
                    int funcTipo = funcoes[iF];
                    String nomeFunc = "";
                    if (funcTipo == 1) nomeFunc = "H_DIV";
                    if (funcTipo == 2) nomeFunc = "H_MUL";
                    if (funcTipo == 3) nomeFunc = "H_FOLD";

                    int iS = 0;
                    while (iS < 3) { 
                        int seed = seeds[iS];
                        
                        executarExperimento(m, n, funcTipo, seed, nomeFunc, true);

                        iS = iS + 1;
                    }
                    iF = iF + 1;
                }
                iN = iN + 1;
            }
            iM = iM + 1;
        }
    }

    public static void executarExperimento(int m, int n, int tipoFunc, int seed, String nomeFunc, boolean imprimir) {
        
        if (imprimir) {
            System.err.println("AUDITORIA: Iniciando " + nomeFunc + " m=" + m + " seed=" + seed);
        }

        Random gerador = new Random(seed);
        
        int[] chaves = new int[n];
        int i = 0;
        while (i < n) {
            int val = gerador.nextInt(900000000);
            val = val + 100000000;
            chaves[i] = val;
            i = i + 1;
        }

        TabelaHash tabela = new TabelaHash(m, tipoFunc);
        long inicioIns = System.currentTimeMillis();
        
        i = 0;
        while (i < n) {
            tabela.inserir(chaves[i]);
            i = i + 1;
        }
        long tempoIns = System.currentTimeMillis() - inicioIns;
        
        long checksumFinal = tabela.getChecksumFinal();
        double mediaColisoesLista = tabela.colisoesListaInsercao / (double) n; 

        int metade = n / 2;
        int[] loteHits = new int[metade];
        int[] loteMisses = new int[metade];
        
        i = 0;
        while (i < metade) {
            loteHits[i] = chaves[i];
            i = i + 1;
        }
        
        i = 0;
        while (i < metade) {
            int val = gerador.nextInt(900000000);
            val = val + 100000000;
            loteMisses[i] = val;
            i = i + 1;
        }
        
        long tempoAcumuladoHits = 0;
        int r = 0;
        while (r < 5) {
            long tInicio = System.currentTimeMillis();
            int j = 0;
            while (j < metade) {
                tabela.buscar(loteHits[j]); 
                j = j + 1;
            }
            long tFim = System.currentTimeMillis();
            tempoAcumuladoHits = tempoAcumuladoHits + (tFim - tInicio);
            r = r + 1;
        }
        double mediaTempoHits = tempoAcumuladoHits / 5.0;

        long tempoAcumuladoMisses = 0;
        r = 0;
        while (r < 5) {
            long tInicio = System.currentTimeMillis();
            int j = 0;
            while (j < metade) {
                tabela.buscar(loteMisses[j]); 
                j = j + 1;
            }
            long tFim = System.currentTimeMillis();
            tempoAcumuladoMisses = tempoAcumuladoMisses + (tFim - tInicio);
            r = r + 1;
        }
        double mediaTempoMisses = tempoAcumuladoMisses / 5.0;

        long mediaCmpHits = tabela.comparacoesBuscaHits / 5;
        long mediaCmpMisses = tabela.comparacoesBuscaMisses / 5;
        
        String sColLst = String.format("%.4f", mediaColisoesLista).replace(',', '.');
        String sTempoHits = String.format("%.4f", mediaTempoHits).replace(',', '.');
        String sTempoMisses = String.format("%.4f", mediaTempoMisses).replace(',', '.');

        if (imprimir) {
            System.out.println(m + "," + n + "," + nomeFunc + "," + seed + "," + 
                tempoIns + "," + 
                tabela.colisoesTabela + "," + 
                sColLst + "," + 
                sTempoHits + "," + 
                sTempoMisses + "," + 
                mediaCmpHits + "," + 
                mediaCmpMisses + "," + 
                checksumFinal
            );
        }
    }
}
