package br.com.fintrack.security;
import io.jsonwebtoken.JwtException; import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.security.authentication.*; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.core.userdetails.*; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter{
 private final JwtService jwt; private final UsuarioDetailsService usuarios;
 public JwtAuthenticationFilter(JwtService j,UsuarioDetailsService u){jwt=j;usuarios=u;}
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
   try{String email=jwt.extrairEmail(h.substring(7));UserDetails user=usuarios.loadUserByUsername(email);UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));SecurityContextHolder.getContext().setAuthentication(auth);}catch(JwtException|UsernameNotFoundException e){SecurityContextHolder.clearContext();}
  } chain.doFilter(req,res);
 }
}
