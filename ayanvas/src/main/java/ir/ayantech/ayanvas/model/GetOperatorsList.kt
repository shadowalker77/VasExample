package ir.ayantech.ayanvas.model

internal data class GetOperatorsListOutput(
    val Footer: String,
    val Header: String,
    val OperatorsList: List<Operators>
)

internal data class Operators(
    val Action: String,
    val Description: String
)