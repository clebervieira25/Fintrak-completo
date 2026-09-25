package br.com.fintrack.service;
import br.com.fintrack.dto.CategoriaDTO; import br.com.fintrack.exception.*; import br.com.fintrack.model.*; import br.com.fintrack.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class CategoriaService{
 private final CategoriaRepository categorias; private final UsuarioRepository usuarios; private final TransacaoRepository transacoes;
 public CategoriaService(CategoriaRepository c,UsuarioRepository u,TransacaoRepository t){categorias=c;usuarios=u;transacoes=t;}
 @Transactional public CategoriaDTO criar(Long uid,CategoriaDTO d){validarNome(uid,d.nome(),null);Usuario u=usuarios.findById(uid).orElseThrow(()->new ResourceNotFoundException("Usuário não encontrado."));return dto(categorias.save(new Categoria(d.nome().trim(),d.cor(),u)));}
 @Transactional(readOnly=true) public List<CategoriaDTO> listar(Long uid){return categorias.findAllByUsuarioIdOrderByNome(uid).stream().map(this::dto).toList();}
 @Transactional public CategoriaDTO atualizar(Long uid,Long id,CategoriaDTO d){Categoria c=buscar(uid,id);validarNome(uid,d.nome(),c);c.atualizar(d.nome().trim(),d.cor());return dto(c);}
 @Transactional public void remover(Long uid,Long id){Categoria c=buscar(uid,id);if(transacoes.existsByCategoriaIdAndUsuarioId(id,uid))throw new ConflitoException("A categoria possui transações e não pode ser removida.");categorias.delete(c);}
 private Categoria buscar(Long uid,Long id){return categorias.findByIdAndUsuarioId(id,uid).orElseThrow(()->new ResourceNotFoundException("Categoria não encontrada."));}
 private void validarNome(Long uid,String nome,Categoria atual){if(categorias.existsByUsuarioIdAndNomeIgnoreCase(uid,nome)&&(atual==null||!atual.getNome().equalsIgnoreCase(nome)))throw new ConflitoException("Já existe uma categoria com esse nome.");}
 private CategoriaDTO dto(Categoria c){return new CategoriaDTO(c.getId(),c.getNome(),c.getCor());}
}
