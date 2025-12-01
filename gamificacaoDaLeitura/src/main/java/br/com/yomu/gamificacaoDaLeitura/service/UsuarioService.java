package br.com.yomu.gamificacaoDaLeitura.service;

import br.com.yomu.gamificacaoDaLeitura.model.Usuario;
import br.com.yomu.gamificacaoDaLeitura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private String salvarImagem(String base64) {
        try {
            if (base64 == null || base64.trim().isEmpty()) {
                throw new IllegalArgumentException("Imagem não pode ser vazia");
            }
            
            String imageString = base64.trim();
            
            // LOG: Ver o que está chegando
            System.out.println("=== DEBUG IMAGEM ===");
            System.out.println("String original (primeiros 100 chars): " + imageString.substring(0, Math.min(100, imageString.length())));
            System.out.println("Contém vírgula? " + imageString.contains(","));
            
            // Remove prefixo data URI se existir
            if (imageString.contains(",")) {
                int commaIndex = imageString.indexOf(",");
                System.out.println("Índice da vírgula: " + commaIndex);
                imageString = imageString.substring(commaIndex + 1);
                System.out.println("Depois do corte (primeiros 50 chars): " + imageString.substring(0, Math.min(50, imageString.length())));
            }
            
            // Remove todos os espaços em branco
            imageString = imageString.replaceAll("\\s+", "");
            
            System.out.println("Depois de remover espaços (primeiros 50 chars): " + imageString.substring(0, Math.min(50, imageString.length())));
            System.out.println("===================");
            
            // Decodifica o base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageString);

            String fileName = UUID.randomUUID() + ".png";
            Path uploadDir = Paths.get("uploads");
            Path filePath = uploadDir.resolve(fileName);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Files.write(filePath, imageBytes);

            return "/uploads/" + fileName;
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Formato de imagem inválido em base64: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage(), e);
        }
    }

    // UsuariosService.java
    @Transactional(readOnly = true)
    public Usuario login(String email, String senha) {
        var usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos"));

        boolean ok = senha.equals(usuario.getSenha()); // <- simples, sem hash

        if (!ok) throw new IllegalArgumentException("Email ou senha inválidos");
        return usuario;
    }

    @Transactional
    public Usuario criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        if (usuarioRepository.existsByNomeUsuario(usuario.getNomeUsuario())) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }
        
        // Gerar código de convite único
        usuario.setCodigoConvite(gerarCodigoConvite());

         if (usuario.getFotoPerfil() != null && usuario.getFotoPerfil().startsWith("data:image")) {
            String url = salvarImagem(usuario.getFotoPerfil());
            usuario.setFotoPerfil(url);
        }
        
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public Usuario buscarPorCodigoConvite(String codigoConvite) {
        return usuarioRepository.findByCodigoConvite(codigoConvite)
            .orElseThrow(() -> new IllegalArgumentException("Código de convite inválido"));
    }

    @Transactional
    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarPorId(id);
        
        if (usuarioAtualizado.getNomeUsuario() != null) {
            usuario.setNomeUsuario(usuarioAtualizado.getNomeUsuario());
        }
        if (usuarioAtualizado.getFotoPerfil() != null) {
            String url = salvarImagem(usuarioAtualizado.getFotoPerfil());
            usuario.setFotoPerfil(url);
        }
        
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void adicionarXp(UUID usuarioId, Long xp) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setXpTotal(usuario.getXpTotal() + xp);
        
        // Calcular novo nível (exemplo: a cada 1000 XP = 1 nível)
        Integer novoNivel = (int) (usuario.getXpTotal() / 1000) + 1;
        usuario.setNivelAtual(novoNivel);
        
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void deletar(UUID id) {
        usuarioRepository.deleteById(id);
    }

    private String gerarCodigoConvite() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}