package cl.duoc.ms.adm.facturas.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import cl.duoc.ms.adm.facturas.dto.S3ObjectDto;
import cl.duoc.ms.adm.facturas.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    // Crear factura (solo metadata, sin archivo)
    @PostMapping("/crear")
    public ResponseEntity<String> crearFactura(@RequestParam String clienteId,
                                               @RequestParam String facturaId,
                                               @RequestParam String fecha) {
        // Aquí puedes guardar la metadata en tu base de datos si lo deseas
        return ResponseEntity.ok("Factura creada correctamente.");
    }

    // Subir factura (archivo PDF) a S3
    @PostMapping("/subir")
public ResponseEntity<String> subirFactura(@RequestParam String clienteId,
                                           @RequestParam String facturaId,
                                           @RequestParam String fecha,
                                           @RequestParam("file") MultipartFile file) {
    try {
        LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
        String carpeta = clienteId + "/" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String key = carpeta + "/" + facturaId + ".pdf";
        awsS3Service.upload("bucketduocpruebas3", key, file);
        return ResponseEntity.ok("Factura subida correctamente a S3.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error al subir factura: " + e.getMessage());
    }
}

    // Modificar/actualizar factura (sobrescribe el archivo en S3)
    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizarFactura(@RequestParam String clienteId,
                                                   @RequestParam String facturaId,
                                                   @RequestParam String fecha,
                                                   @RequestParam("file") MultipartFile file) {
        LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
        String carpeta = clienteId + "/" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String key = carpeta + "/" + facturaId + ".pdf";
        awsS3Service.upload("bucketduocpruebas3", key, file);
        return ResponseEntity.ok("Factura actualizada correctamente en S3.");
    }

    // Descargar factura
    @GetMapping("/descargar")
    public ResponseEntity<byte[]> descargarFactura(@RequestParam String clienteId,
                                                  @RequestParam String facturaId,
                                                  @RequestParam String fecha) {
        LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
        String carpeta = clienteId + "/" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String key = carpeta + "/" + facturaId + ".pdf";
        byte[] fileBytes = awsS3Service.downloadAsBytes("bucketduocpruebas3", key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + facturaId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }

    // Eliminar factura
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarFactura(@RequestParam String clienteId,
                                                 @RequestParam String facturaId,
                                                 @RequestParam String fecha) {
        LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
        String carpeta = clienteId + "/" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String key = carpeta + "/" + facturaId + ".pdf";
        awsS3Service.deleteObject("bucketduocpruebas3", key);
        return ResponseEntity.ok("Factura eliminada correctamente de S3.");
    }

    // Consultar historial de facturas para un cliente (devuelve lista de archivos)
    @GetMapping("/historial")
    public ResponseEntity<List<S3ObjectDto>> historialFacturas(@RequestParam String clienteId) {
        // Listar todos los objetos bajo la carpeta del cliente
        String prefix = clienteId + "/";
        List<S3ObjectDto> archivos = awsS3Service.listObjectsWithPrefix("bucketduocpruebas3", prefix);
        return ResponseEntity.ok(archivos);
    }
}
