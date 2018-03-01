import math

class VectorN(object):
    def __init__(self, *args):
        self.mData = []
        self.mDim = 0
        if(len(args) == 2):
            self.__class__ = Vector2
        elif(len(args) == 3):
            self.__class__ = Vector3
        for arg in args:
            try:
                self.mData.append(float(arg))
            except TypeError:
                raise ValueError('You can only pass scalar numbers to a VectorN!')
            self.mDim += 1
    def __str__(self):
        s = "<Vector" + str(self.mDim) + ": "
        for n in self.mData:
            s += str(n) + ", "
        s = s[0:-2] + ">"
        return s

    def __len__(self):
        return self.mDim

    def __getitem__(self, idx):
        if not isinstance(idx, int):
            raise ValueError("Index must be an integer.")
        return self.mData[idx]

    def __setitem__(self, idx, val):
        if not isinstance(idx, int):
            raise ValueError("Index must be an integer.")
        try:
            self.mData[idx] = float(val)
        except TypeError:
            raise ValueError('You can only pass numbers to a VectorN!')
    def __eq__(self, rhs):
        if not isinstance(rhs, VectorN):
            return False
        if self.mData != rhs.mData:
            return False
        for i in range(self.mDim):
            if self.mData[i] != rhs.mData[i]:
                return False
        return True
    def copy(self):
        return VectorN(*self.mData)

    @property
    def i(self):
        L = []
        for n in self.mData:
            L.append(int(n))
        return tuple(L)

    def __add__(self, rhs):
        if not isinstance(rhs, VectorN):
            raise ValueError("Can only add two VectorNs! You passed: " + str(rhs))
        if self.mDim != rhs.mDim:
            raise ValueError("Can only add VectorNs in the same dimension")
        L = []
        for i in range(self.mDim):
            L.append(self.mData[i] + rhs.mData[i])
        return VectorN(*L)

    def __sub__(self, rhs):
        if not isinstance(rhs, VectorN):
            raise ValueError("Can only subtract a VectorN from another VectorN! You passed: " + str(rhs))
        if self.mDim != rhs.mDim:
            raise ValueError("Can only subtract VectorNs in the same dimension")
        L = []
        for i in range(self.mDim):
            L.append(self.mData[i] - rhs.mData[i])
        return VectorN(*L)

    def __mul__(self, rhs):
        if (isinstance(rhs, int) or isinstance(rhs, float)):            
            L = []
            for i in range(self.mDim):
                L.append(self.mData[i] * rhs)
            return VectorN(*L)
        else:
            return NotImplemented

    def __rmul__(self, lhs):
        return self * lhs

    def __truediv__(self, rhs):
        if not (isinstance(rhs, int) or isinstance(rhs, float)):
            raise ValueError("Can only divide a VectorN by a scalar! You passed: " + str(rhs))
        L = []
        for i in range(self.mDim):
            L.append(self.mData[i] / rhs)
        return VectorN(*L)
    
    def __neg__(self):
        L = []
        for n in self.mData:
            L.append(-n)
        return VectorN(*L)

    def magnitude(self):
        total = 0
        for n in self.mData:
            total += n * n
        return math.sqrt(total)

    def magnitudeSquared(self):
        total = 0
        for n in self.mData:
            total += n * n
        return total

    def normalized(self):
        if self.isZero():
            L  = []
            for i in range(self.mDim):
                L.append(0)
            return VectorN(*L)
        return self / self.magnitude()

    def isZero(self):
        for n in self.mData:
            if n != 0:
                return False
        return True

    def truncate(self, length):
        if(length > self.mDim):
            raise ValueError("Truncation length must be <= the dimension of the vector")
        return VectorN(*self.mData[:length])

    def dot(self, other):
        if (not isinstance(other, VectorN) and not isinstance(other, Vector2) and not isinstance(other, Vector3)):
            raise TypeError("Can only calculate dot product of two vectors!")
        if self.mDim != other.mDim:
            raise ValueError("Can only calculate dot product if both vectors have the same dimension.")
        s = 0
        for i in range(self.mDim):
            s += self.mData[i] * other.mData[i]
        return s
    
    def cross(self, other):
        if (not isinstance(other, VectorN)  and not isinstance(other, Vector3)):
            raise TypeError("Can only calculate cross product of two Vector3s!")
        if self.mDim != 3 or other.mDim != 3:
            raise ValueError("Can only calculate cross product if both vectors have a dimension of three")
        v = Vector3(0,0,0)
        v[0] = self[1] * other[2] - self[2] * other[1]
        v[1] = self[2] * other[0] - self[0] * other[2]
        v[2] = self[0] * other[1] - self[1] * other[0]
        return v

    def pairwiseMult(self, other):
        if not isinstance(other, VectorN) or other.mDim != self.mDim:
            raise ValueError("Must pass a vector of the same dimension!")
        res = self.copy()
        for i in range(self.mDim):
            res[i] = self[i] * other[i]
        return res

    def clamp(self, mi = 0, ma = 1):
        res = self.copy()
        for i in range(self.mDim):
            if res[i] < mi:
                res[i] = mi
            elif res[i] > ma:
                res[i] = ma
        return res
    
class Vector2(VectorN):
    def __init__(self, *args):
        self.mData = []
        self.mDim = 0
        if(len(args) != 2):
            raise ValueError("Vector2 must be passed two arguments!")
        for arg in args:
            try:
                self.mData.append(float(arg))
            except TypeError:
                raise ValueError('You can only pass scalar numbers to a VectorN!')
            self.mDim += 1
    @property
    def x(self):
        return self.mData[0]
    @x.setter
    def x(self, x):
        try:
            self.mData[0] = float(x)
        except TypeError:
            raise ValueError('You can only pass scalar numbers to a VectorN!') 
    @property
    def y(self):
        return self.mData[1]
    @y.setter
    def y(self, y):
        try:
            self.mData[1] = float(y)
        except TypeError:
            raise ValueError('You can only pass scalar numbers to a VectorN!')
    @property
    def radians(self):
        return math.atan2(self.mData[1], self.mData[0])
    @property
    def degrees(self):
        return math.degrees(self.radians)

    def copy(self):
        return Vector2(*self.mData)

    @property
    def perp(self):
        v = VectorN(self.x, self.y, 0)
        w = VectorN(0, 0, 1)
        return Vector2(*v.cross(w).truncate(2).mData)


    
class Vector3(VectorN):
    def __init__(self, *args):
        self.mData = []
        self.mDim = 0
        if(len(args) != 3):
            raise ValueError("Vector3 must be passed three arguments!")
        for arg in args:
            try:
                self.mData.append(float(arg))
            except TypeError:
                raise ValueError('You can only pass scalar numbers to a VectorN!')
            self.mDim += 1
    @property
    def x(self):
        return self.mData[0]
    @x.setter
    def x(self, x):
        try:
            self.mData[0] = float(x)
        except TypeError:
            raise Exception('You can only pass scalar numbers to a VectorN!') 
    @property
    def y(self):
        return self.mData[1]
    @y.setter
    def y(self, y):
        try:
            self.mData[1] = float(y)
        except TypeError:
            raise Exception('You can only pass scalar numbers to a VectorN!')

    @property
    def z(self):
        return self.mData[2]
    @z.setter
    def z(self, z):
        try:
            self.mData[2] = float(z)
        except TypeError:
            raise Exception('You can only pass scalar numbers to a VectorN!')
        
    @property
    def radians(self):
        return math.atan2(self.mData[1], self.mData[0])
    @property
    def degrees(self):
        return math.degrees(self.radians)

    def copy(self):
        return Vector3(*self.mData)


def polar_to_vector2(rads, dist):
    return Vector2(dist * math.cos(rads), dist * math.sin(rads))
print(VectorN)

if __name__ == "__main__":
    v = VectorN(4, 7, -3)
    w = VectorN(2, 0, 6)
    q = VectorN(5, 9, -12)
    p = VectorN(0, 0, 0, 0, 0, 0)
    print(v + w) # <Vector3: 6.0, 7.0, 3.0>
    print(v + w + q) # <Vector3: 11.0, 16.0, -9.0>
    #print(v + 7) # ValueError: You can only add another Vector3 to this Vector3 (you passed '7').
    print(v - w) # <Vector3: 2.0, 7.0, -9.0>
    #print(v - "abc") # ValueError: You can only subtract another Vector3 from this Vector3 (you passed 'abc').
    print(v * 2) # <Vector3: 8.0, 14.0, -6.0>
    print(3 * v) # <Vector3: 12.0, 21.0, -9.0>
    print(v / 2) # <Vector3: 2.0, 3.5, -1.5>
    #print(v / w) # ValueError: You can only divide this Vector3 by a scalar.
     # You attempted to divide by '<Vector3: 2.0, 0.0, 6.0>'.
    #print(2 / v) # TypeError: unsupported operand type(s) for /: 'int' and 'VectorN'
    # Note: We'll do something like multiplication between two vectors, but there are many types of vector-vector
    # "multiplication" (dot-product, cross-product, etc.). To avoid confusion, let’s dis-allow vector * vector
    #print(v * w) # ValueError: You can only multiply this Vector3 and a scalar.
     # You attempted to multiply by '<Vector3: 2.0, 0.0, 6.0>'.
    print(-v) # <Vector3: -4.0, -7.0, 3.0>
    print(v.magnitude()) # 8.602325267042627
    print(v.magnitudeSquared()) # 74.0
    print(v.normalized()) # <Vector3: 0.46499055497527714, 0.813733471206735, -0.34874291623145787>
    print(v.normalized().magnitude()) # 1.0
    print(q.isZero()) # False
    print(p.isZero()) # True

    v = VectorN(1,2,3)
    w = VectorN(4,5,6)
    print(v.dot(w))
    print(v.cross(w))
    v = Vector2(1,0)
    print(v.perp)

    v = Vector3(3,7,5)
    w = Vector3(5,1,4)
    q = Vector3(8,1,2)
    d = 3
    
    print(q.dot(v + d*w))
    print(q.dot(v) + q.dot(d *w))
    print(q.dot(v) + d * q.dot(w))
