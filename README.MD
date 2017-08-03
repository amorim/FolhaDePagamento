# Folha de Pagamento

#### 1. Instruções para executar o programa pela primeira vez
Existem duas formas de executar o projeto. Você pode abri-lo como um projeto do netbeans, apenas fazendo o clone deste repositório, e em seguida executar. Você também pode utilizar o jar compilado encontrado no diretório dist. É importante notar que o diretório _lib_ é de extrema importância para o funcionamento do programa, então mantenha a estrutura da pasta dist como foi distribuída.
O projeto depende de apenas uma biblioteca, já disponibilizamos o jar dela em _dist/lib/sqljdbc42.jar_ para maior comodidade.

### 2. Informações
Utilize as credenciais abaixo para acesso completo ao sistema:
```
Usuário: admin
Senha: 1234 
```
O programa fará todas as consultas de um servidor remoto. Esse procedimento pode ser ligeiramente lento dependendo da conexão usada, por isso, recomendamos que aguarde alguns instantes antes de fechar o programa à força. Devido ao tempo limitado não foi possível implementar mecanismos de multi-threading.

### 3. O Programa
O programa cumpre todos os requisitos solicitados. Segue:

| Requisito | No programa |
| --- | --- |
| Adição de empregado | Insert Employee |
| Remoção de empregado | Remove Employee |
| Bater o Ponto | Register Access |
| Lançar venda | Register Sale |
| Lançar taxa de serviço | Register Fee |
| Modificação de empregado | Edit Employee |
| Undo/Redo | Dataset Manager |
| Rodar a Folha | Generate Payroll |
| Selecionar/criar agenda de pagamento | Payment Schedule |

A utilização da grande maioria dessas funcionalidades é totalmente intuitiva. Convém, entretanto, detalhar o funcionamento de algumas:
* Bater o Ponto
O sistema utiliza a data atual do sistema para registrar o ponto, e registra apenas duas vezes ao dia (entrada e saída).

* Undo/Redo
O DataSet Manager está localizado na tela de login. Clicando nele você tem acesso ao que chamamos de _snapshots_. Para retornar para um ponto anterior, basta selecionar o _snapshot_ desejado e clicar no botão, aguarde alguns segundos e os dados serão restaurados exatamente como estavam antes da ocorrência declarada. Você está livre para ir e voltar tempo com relação aos _snapshots_, mas atenção, ao realizar algum tipo de alteração nos dados de um _snapshot_ mais antigo, todos os _snapshots_ posteriores serão imediatamente excluídos. Isso é feito para garantir a integridade dos dados.

* Rodar a folha
Quando a folha é rodada para aquele dia, o programa gera um arquivo HTML contendo alguns detalhes com respeito aos pagamentos, o arquivo chama-se _folha.html_ e é gerado no diretório de execução do programa.
