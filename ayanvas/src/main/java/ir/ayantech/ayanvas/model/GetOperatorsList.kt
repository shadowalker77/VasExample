package ir.ayantech.ayanvas.model

data class GetOperatorsListOutput(
    val Footer: String,
    val Header: String,
    val OperatorsList: List<Operators>
)

data class Operators(
    val Action: String,
    val Description: String
)