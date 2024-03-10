package com.crow.gradle.plugins.poetry.tasks.idea

import java.io.File
import org.dom4j.Node
import org.dom4j.io.DOMWriter
import org.dom4j.io.SAXReader
import org.intellij.lang.annotations.Language
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS

/**
 * IntelliJ Idea xml parser.
 */
class XmlParser(private val inputXml: File) {

	/** Xml reader/deserializer. */
	private val xmlReader = SAXReader()

	/** Xml document to modify. */
	private val document = xmlReader.read(inputXml)

	/** Dom's implementation used to create xml writer and output destination. */
	private val dom = DOMImplementationRegistry.newInstance().getDOMImplementation("LS") as DOMImplementationLS

	/** Xml writer/serializer. */
	private val xmlWriter = dom.createLSSerializer()

	init {
		xmlWriter.newLine = "\n"
		xmlWriter.domConfig.setParameter("format-pretty-print", true)
		xmlWriter.domConfig.setParameter("xml-declaration", true)
		xmlWriter.domConfig.setParameter("element-content-whitespace", true)
	}

	/**
	 * Parse xml document.
	 * @param expressionXpath xpath expression to parse.
	 * @return list of selected nodes.
	 */
	fun parse(@Language("XPath") expressionXpath: String? = null): List<Node> {
		if (expressionXpath == null) return listOf(document.node(0))
		val xpath = document.createXPath(expressionXpath)
		return xpath.selectNodes(document)
	}

	/**
	 * Write xml document to file.
	 */
	fun write() {
		val input = DOMWriter().write(document)
		val output = dom.createLSOutput()
		output.byteStream = inputXml.outputStream()
		output.encoding = "UTF-8"
		xmlWriter.write(input, output)
	}
}
