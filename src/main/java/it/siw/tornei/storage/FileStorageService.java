package it.siw.tornei.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Servizio per salvare i file caricati dagli utenti (es. loghi delle squadre).
 *
 * Funziona cosi':
 *  - Al primo avvio crea la cartella 'uploads' nella root del progetto
 *  - save(MultipartFile) salva il file con un nome UUID casuale (evita collisioni)
 *    e ritorna il path "web" /uploads/nome.ext da mettere nel DB
 *  - delete(webPath) cancella il file da disco quando l'entita' viene eliminata
 *
 * Il path "web" viene poi servito come risorsa statica grazie a WebConfig
 * (vedi addResourceHandlers).
 */
@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare cartella upload", e);
        }
    }

    /**
     * Salva il file su disco con un nome univoco e ritorna il path web.
     * @return path web tipo "/uploads/abc-123.png" o null se file vuoto
     */
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;
        Path target = rootLocation.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Errore salvataggio file: " + e.getMessage(), e);
        }
        return "/uploads/" + filename;
    }

    public void delete(String webPath) {
        if (webPath == null) return;
        // Se non e' un path locale (es. URL esterno https://... salvato dal DataSeeder),
        // non c'e' alcun file su disco da cancellare -- e Files.resolve esploderebbe
        // con InvalidPathException perche' Windows non ammette ':' nei nomi file.
        if (!webPath.startsWith("/uploads/")) return;
        String filename = webPath.substring("/uploads/".length());
        try { Files.deleteIfExists(rootLocation.resolve(filename)); }
        // Catch generico: InvalidPathException non e' un IOException, quindi va
        // aggiunta esplicitamente. Meglio prevenire ulteriori tipi di errori qui:
        // la delete di un file logo non deve MAI far fallire l'operazione principale.
        catch (Exception ignored) {}
    }
}
