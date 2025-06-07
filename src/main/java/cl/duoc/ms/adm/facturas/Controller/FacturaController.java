package cl.duoc.ms.adm.facturas.Controller;

import cl.duoc.ms.adm.facturas.dto.FacturaDTO;
import cl.duoc.ms.adm.facturas.dto.DetalleFacturaDTO;
import cl.duoc.ms.adm.facturas.model.Factura;
import cl.duoc.ms.adm.facturas.service.FacturaService;
import cl.duoc.ms.adm.facturas.model.DetalleFactura;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/facturas")
public class FacturaController {
    private final FacturaService facturaService;
 


    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<FacturaDTO> listar() {
        return facturaService.listarFacturas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<FacturaDTO> historialPorCliente(@PathVariable Long clienteId) {
        return facturaService.obtenerFacturasPorCliente(clienteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public FacturaDTO agregar(@RequestBody FacturaDTO facturaDTO) {
        Factura factura = toEntity(facturaDTO);
        Factura guardada = facturaService.guardarFactura(factura);
        return toDTO(guardada);
    }

    @PutMapping("/{id}")
    public FacturaDTO actualizar(@PathVariable Long id, @RequestBody FacturaDTO facturaDTO) {
        Factura factura = toEntity(facturaDTO);
        factura.setId(id);
        Factura actualizada = facturaService.guardarFactura(factura);
        return toDTO(actualizada);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
    }

    private FacturaDTO toDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setClienteId(factura.getClienteId());
        dto.setFecha(factura.getFecha());
        dto.setTotal(factura.getTotal());
        dto.setArchivoPath(factura.getArchivoPath());
        if (factura.getDetalles() != null) {
            dto.setDetalles(factura.getDetalles().stream()
                    .map(this::toDetalleDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private Factura toEntity(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setId(dto.getId());
        factura.setClienteId(dto.getClienteId());
        factura.setFecha(dto.getFecha());
        factura.setTotal(dto.getTotal());
        factura.setArchivoPath(dto.getArchivoPath());
        if (dto.getDetalles() != null) {
            factura.setDetalles(dto.getDetalles().stream()
                    .map(this::toDetalleEntity)
                    .collect(Collectors.toList()));
        }
        return factura;
    }

    private DetalleFacturaDTO toDetalleDTO(DetalleFactura detalle) {
        DetalleFacturaDTO dto = new DetalleFacturaDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProductoId());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    private DetalleFactura toDetalleEntity(DetalleFacturaDTO dto) {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setId(dto.getId());
        detalle.setProductoId(dto.getProductoId());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setCantidad(dto.getCantidad());
        detalle.setSubtotal(dto.getSubtotal());
        return detalle;
    }
}