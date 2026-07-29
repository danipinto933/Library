const Button = ({ children, onClick, type = "button", className = "", disabled = false, style = {} }) => {
    return (
        <button
            type={type}
            onClick={onClick}
            className={className}
            disabled={disabled}
            style={style}
        >
            {children}
        </button>
    )
}

export default Button;