/**
 * Repository
 *
 * Questa classe rappresenta un repository GitHub.
 * Contiene le informazioni principali di un repository, inclusi il nome,
 * la descrizione, la lingua, le statistiche come stelle e fork, e i dettagli del proprietario.
 *
 * @property id Identificativo unico del repository.
 * @property name Nome del repository.
 * @property description Descrizione opzionale del repository.
 * @property language Linguaggio principale utilizzato nel repository (opzionale).
 * @property stars Numero di stelle ricevute dal repository.
 * @property forks Numero di fork creati dal repository.
 * @property ownerImageUrl URL dell'immagine del profilo del proprietario del repository (opzionale).
 * @property ownerName Nome del proprietario del repository.
 */

data class Repository(
    val id: Int, // Identificativo univoco del repository
    val name: String, // Nome del repository
    val description: String?, // Descrizione del repository (può essere null)
    val language: String?, // Linguaggio principale del repository (può essere null)
    val stars: Int, // Numero di stelle assegnate al repository
    val forks: Int, // Numero di fork del repository
    val ownerImageUrl: String?, // URL dell'immagine del profilo del proprietario (può essere null)
    val ownerName: String // Nome del proprietario del repository
)