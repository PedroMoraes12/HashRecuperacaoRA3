package hash_rec3;

public class TabelaHash {

    // Constantes para os tipos de hash
    public static final int H_DIV = 1;
    public static final int H_MUL = 2;
    public static final int H_FOLD = 3;

    // Estruturas de dados
    public No[] tabela;
    public int tamanhoM;
    public int tipoHash;
    
    // Métricas
    public long colisoesTabela;
    public double colisoesListaInsercao;
    public long comparacoesBuscaHits;
    public long comparacoesBuscaMisses;
    
    // Auditoria Checksum
    public long checksum;
    public int contadorInsercoes;

    // Constante A para multiplicação
    public final double CONSTANTE_A = 0.6180339887;

    public TabelaHash(int tamanho, int tipo) {
        tamanhoM = tamanho;
        tipoHash = tipo;
        tabela = new No[tamanhoM];
        
        colisoesTabela = 0;
        colisoesListaInsercao = 0;
        comparacoesBuscaHits = 0;
        comparacoesBuscaMisses = 0;
        checksum = 0;
        contadorInsercoes = 0;
    }

    // FUNÇÕES DE HASH

    // 1. Divisão: k mod m
    public int hashDivisao(int k) {
        int indice = k % tamanhoM;
        if (indice < 0) {
            indice = indice * -1;
        }
        return indice;
    }

    // 2. Multiplicação: floor(m * frac(k * A))
    public int hashMultiplicacao(int k) {
        double produto = k * CONSTANTE_A;
        long parteInteira = (long) produto; // Cast para simular floor/int
        double fracionaria = produto - parteInteira;
        
        double indiceDouble = tamanhoM * fracionaria;
        return (int) indiceDouble;
    }

    // 3. Dobramento: blocos de 3 dígitos somados
    public int hashDobramento(int k) {
        
        int p1 = k % 1000;
        
        int temp = k / 1000;
        
        int p2 = temp % 1000;
        
        int p3 = temp / 1000;
        
        int soma = p1 + p2;
        soma = soma + p3;
        
        int indice = soma % tamanhoM;
        if (indice < 0) {
            indice = indice * -1;
        }
        return indice;
    }

    // Roteador de funções hash
    public int calcularHash(int k) {
        int h = 0;
        if (tipoHash == H_DIV) {
            h = hashDivisao(k);
        } else {
            if (tipoHash == H_MUL) {
                h = hashMultiplicacao(k);
            } else {
                h = hashDobramento(k);
            }
        }
        
        if (contadorInsercoes < 10) {
            checksum = checksum + h;
            contadorInsercoes = contadorInsercoes + 1;
        }
        
        return h;
    }

    // OPERAÇÕES
    public void inserir(int k) {
        int indice = calcularHash(k);
        
        No novoNo = new No(k);
        
        if (tabela[indice] == null) {
            tabela[indice] = novoNo;
        } else {
            colisoesTabela = colisoesTabela + 1;
            
            No atual = tabela[indice];
            int nosPercorridos = 0;
            
            boolean temProximo = true;
            if (atual.proximo == null) {
                temProximo = false;
            }
            
            while (temProximo) {
                nosPercorridos = nosPercorridos + 1;
                atual = atual.proximo;
                
                if (atual.proximo == null) {
                    temProximo = false;
                }
            }
            
            atual.proximo = novoNo;
            
            colisoesListaInsercao = colisoesListaInsercao + nosPercorridos;
        }
    }

    public void buscar(int k) {
        int indice = 0;
        
        if (tipoHash == H_DIV) {
            indice = hashDivisao(k);
        } else {
            if (tipoHash == H_MUL) {
                indice = hashMultiplicacao(k);
            } else {
                indice = hashDobramento(k);
            }
        }

        No atual = tabela[indice];
        int comparacoes = 0;
        boolean encontrado = false;
        
        boolean continuar = true;
        if (atual == null) {
            continuar = false;
        }
        
        while (continuar) {
            comparacoes = comparacoes + 1;
            
            if (atual.chave == k) {
                encontrado = true;
                continuar = false;
            } else {
                atual = atual.proximo;
                if (atual == null) {
                    continuar = false;
                }
            }
        }

        if (encontrado) {
            comparacoesBuscaHits = comparacoesBuscaHits + comparacoes;
        } else {
            comparacoesBuscaMisses = comparacoesBuscaMisses + comparacoes;
        }
    }
    
    public long getChecksumFinal() {
        return checksum % 1000003;
    }
}
