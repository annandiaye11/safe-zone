export const AuthGuardGuard = () => {
    console.log('AuthGuardGuard')
    console.log(localStorage.getItem('user-token'))

    return localStorage.getItem('user-token') === null
}
