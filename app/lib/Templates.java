package lib;

import java.lang.reflect.*;
import play.mvc.Content;
import play.mvc.Result;
import play.mvc.Results;
import play.api.templates.*;

public class Templates
{

  public static Result result( String path, Object... params )
  {
    try
    {
      Class cl = Class.forName( path );
      Method render = getRenderMethod( cl );
      if ( render==null )
      {
        return Results.notFound( notFound( path ) );
      }
      return Results.ok( (Content)render.invoke( null, params ) );
    }
    catch( Throwable e )
    {
      play.Logger.error( e.toString(), e );
      return Results.internalServerError( error( path, e ) );
    }
  }

  public static Content render( String path, Object... params )
  {
    try
    {
      Class cl = Class.forName( path );
      Method render = getRenderMethod( cl );
      if ( render==null )
      {
        return notFound( path );
      }
      return (Content)render.invoke( null, params );
    }
    catch( Throwable e )
    {
      play.Logger.error( e.toString(), e );
      return error( path, e );
    }
  }

  public static Method getRenderMethod( Class cl )
  {
    return getMethod( cl, "render" );
  }

  public static Method getMethod( Class cl, String methodName )
  {
    Method[] methods = cl.getMethods();
    for ( Method method : methods )
    {
      if ( method.getName().equals(methodName) )
      {
        return method;
      }
    }
    return null;
  }

  public static Content notFound( String path )
  {
    if ( path.indexOf("views.txt")==0 )
      return new Txt( "Template not found: " + path );
    if ( path.indexOf("views.xml")==0 )
      return new Xml( "<Error><Message>Template not found</Message><Path>" + path + "</Path></Error>");
    return Html.apply( "<p>Template not found: " + path +"</p>" );
  }

  public static Content error( String path, Throwable message )
  {
    if ( path.indexOf("views.txt")==0 )
      return new Txt( "Error: " + message.toString() + " for " + path );
    if ( path.indexOf("views.xml")==0 )
      return new Xml( "<Error><Message>" + message.toString() + "</Message><Path>" + path + "</Path></Error>");
    return Html.apply( "<p>Error: " + message.toString() + " for " + path +"</p>" );
  }


}