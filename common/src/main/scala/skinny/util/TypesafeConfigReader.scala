package skinny.util

import com.typesafe.config._
import scala.collection.JavaConverters._
import scala.util.Try

object TypesafeConfigReader {

  /**
   * Loads a configuration file.
   *
   * @param resource file resource
   * @return config
   */
  def load(resource: String): Config = ConfigFactory.load(getClass.getClassLoader, resource)

  /**
   * Loads a configuration file as Map object.
   *
   * @param resource file resource
   * @return Map object
   */
  def loadAsMap(resource: String): Map[String, String] = fromConfigToMap(load(resource))

  /**
   * Loads a Map object from Typesafe-config object.
   *
   * @param config config
   * @return Map object
   */
  def fromConfigToMap(config: Config): Map[String, String] = {
    def extract(map: java.util.Map[String, Any]): Map[String, String] = {
      map.asScala.flatMap {
        case (parentKey, value: java.util.Map[_, _]) =>
          extract(value.asInstanceOf[java.util.Map[String, Any]]).map { case (k, v) => s"${parentKey}.${k}" -> v }
        case (key, value) => Map(key -> value)
      }
    }.map { case (k, v) => k -> v.toString }.toMap

    config.root().keySet().asScala.flatMap { parentKey =>
      config.root().unwrapped().get(parentKey) match {
        case map: java.util.Map[_, _] =>
          extract(config.root().unwrapped().asInstanceOf[java.util.Map[String, Any]])
        case value =>
          Map(parentKey -> value)
      }
    }.map { case (k, v) => k -> v.toString }.toMap
  }

  def boolean(path: String): Option[Boolean] = Try(ConfigFactory.load().getBoolean(path)).toOption
  def booleanSeq(path: String): Option[Seq[Boolean]] = Try(ConfigFactory.load().getBooleanList(path).asScala.map(_.asInstanceOf[Boolean])).toOption
  def double(path: String): Option[Double] = Try(ConfigFactory.load().getDouble(path)).toOption
  def doubleSeq(path: String): Option[Seq[Double]] = Try(ConfigFactory.load().getDoubleList(path).asScala.map(_.asInstanceOf[Double])).toOption
  def int(path: String): Option[Int] = Try(ConfigFactory.load().getInt(path)).toOption
  def intSeq(path: String): Option[Seq[Int]] = Try(ConfigFactory.load().getIntList(path).asScala.map(_.asInstanceOf[Int])).toOption
  def long(path: String): Option[Long] = Try(ConfigFactory.load().getLong(path)).toOption
  def longSeq(path: String): Option[Seq[Long]] = Try(ConfigFactory.load().getLongList(path).asScala.map(_.asInstanceOf[Long])).toOption
  def string(path: String): Option[String] = Try(ConfigFactory.load().getString(path)).toOption
  def stringSeq(path: String): Option[Seq[String]] = Try(ConfigFactory.load().getStringList(path).asScala).toOption
  def get(path: String): Option[ConfigValue] = Try(ConfigFactory.load().getValue(path)).toOption

}
